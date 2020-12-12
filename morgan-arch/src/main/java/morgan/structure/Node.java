/*
 * Usage: base class of distributed morgan.nodes, classes inherited from this class become a distributed node.
 */
package morgan.structure;

import morgan.messages.IConstMessage;
import morgan.structure.serialize.IConstDistrClass;
import morgan.structure.serialize.InputStream;
import morgan.support.Log;
import morgan.support.Utils;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * <p>
 * 
 * @author Mark D</a>
 * @version 1.0, 2019.12.5
 */
public class Node {

    private String _name;
    private String _addr;

    private NodeEngines _group = new NodeEngines(20);

    private final ZMQ.Context _c = ZMQ.context(3);
    private final ZMQ.Socket _puller = _c.socket(SocketType.PULL);

    private Map<String, Worker> _all_workers = new ConcurrentHashMap<>();
    private ConcurrentLinkedQueue<Worker> _dispachable_workers = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Call> _call_queue = new ConcurrentLinkedQueue<>();

    /*key: method name, value: node name*/
    //TODO call dest's value should be a list of morgan.nodes, make it happen in future
    private Map<String, String> _call_dest = new HashMap<>();
    private Map<String, RemoteNode> _remote_node = new HashMap<>();

	private IConstMessage messageMap_;
	private IConstDistrClass distrMap_;

    public Node(String name, String addr){
        _puller.bind(addr);
        _name = name;
        _addr = addr;
    }

    public void startUp(){
        _group.bindAndRun(null, ()->{
            byte[] bytes = _puller.recv();
            if (bytes == null)
                return;
            InputStream in = new InputStream(bytes);
            Call call = in.read();
            _call_queue.add(call);
            in.reset();
        }, null);

        _group.bindAndRun(null, this::pulse, null);

        for (int i = 0; i < 10; i++){
            _group.bindAndRun(null, ()->{
                Worker worker = _dispachable_workers.poll();
                if (worker == null){
                    return;
                }

                if (!worker.dispatchable()) {
                    _dispachable_workers.add(worker);
                    return;
                }

                try {
                    worker.onStart();
                    worker.pulse();
                    worker.onEnd();
                } catch (Exception e){
                    e.printStackTrace();
                }
                _dispachable_workers.add(worker);
            }, null);
        }
    }

    private void addLocalMethod(Worker worker){
        List<String> methods = worker.getMethods();
        for (var m : methods)
            _call_dest.put(m, _name);
    }

    public void addWorker(Worker worker){
        _all_workers.put(worker.getName(), worker);
        _dispachable_workers.add(worker);
        addLocalMethod(worker);
    }

    public void addWorkerStandAlone(Worker worker){
        _all_workers.put(worker.getName(), worker);
        _group.bindAndRun(null, worker::pulse, null);
        addLocalMethod(worker);
    }

    public void setEngineNum(int num){
        _group.setEngineNum(num);
    }

    public void manualConnect(String name, String addr){
        RemoteNode rn = new RemoteNode(this, name, addr);
        _remote_node.put(name, rn);
        Call call = new Call();
        call.callType = Call.CALL_TYPE_FORM_CONN;
        call.from = _name;
        call.method = _addr;
        call.dest = name;

        sendCall(call);
    }

    private void pulse(){
        pulseRemotes();

        handleCall();
    }

    private void handleCall(){
        Call call = _call_queue.poll();
        if (call == null)
            return;

        switch (call.callType){
            case Call.CALL_TYPE_RPC:
            case Call.CALL_TYPE_RPC_RETURN:
                if (!_remote_node.containsKey(call.from))
                    return;
                String[] args = call.method.split("&");
                if (!_all_workers.containsKey(args[0])){
                    Log.node.error("can't find call handler, type:{}, from:{}, to:{}, method:{}", call.callType, call.from, call.dest, call.method);
                    return;
                }
                _all_workers.get(args[0]).addCallQueue(call);
                break;

            case Call.CALL_TYPE_FORM_CONN:
                onFormConnection(call);
                break;

            case Call.CALL_TYPE_OFFLINE:
                if (!_remote_node.containsKey(call.from))
                    return;
                _remote_node.get(call.from).connClose();
                _remote_node.remove(call.from);
                break;

            case Call.CALL_TYPE_PING:
                if (!_remote_node.containsKey(call.from))
                    return;
                _remote_node.get(call.from).onPing();
                break;

            case Call.CALL_TYPE_COLLECT:
                if (!_remote_node.containsKey(call.from))
                    return;
                onCollect(call);
                break;
        }
    }

    private void pulseRemotes(){
        for (var e : _remote_node.entrySet()){
            if (e.getValue().closed) {
                _remote_node.remove(e.getKey());
                break;
            }
            e.getValue().pulse();
        }
    }

    private void onFormConnection(Call call){
        if (_remote_node.containsKey(call.from))
            return;
        RemoteNode rn = new RemoteNode(this, call.from, call.method);
        _remote_node.put(call.from, rn);

        Call collectCall = new Call();
        collectCall.from = _name;
        collectCall.dest = call.from;
        collectCall.callType = Call.CALL_TYPE_COLLECT;
        collectCall.parameters = getLocalMethods().toArray();

        sendCall(collectCall);
        Log.node.info("morgan.connection established, target:{}", call.from);
    }

    private void onCollect(Call call){
        for (var m : call.parameters)
            _call_dest.put((String)m, call.from);
        Log.node.info("methods collected, from:{}", call.from);
    }

    public void sendCall(Call call){
        if (call.dest.equals(_name)) {
            String[] args = call.method.split("&");

            //local call, local handle
            if (_all_workers.containsKey(args[0])) {
                _all_workers.get(args[0]).addCallQueue(call);
                return;
            }
        }
        if (!_remote_node.containsKey(call.dest)){
            Log.node.error("No Such Remote Node! Error Call. node:{}, worker:{}, dest:{}", _name, call.caller, call.dest);
            return;
        }
        _remote_node.get(call.dest).sendCall(call);
    }

    public List<String> getLocalMethods(){
        List<String> methods = new ArrayList<>();
        for (var worker : _all_workers.values()){
            methods.addAll(worker.getMethods());
        }
        return methods;
    }

    public String getReceiver(String method){
        String dest = _call_dest.get(method);
        if (dest == null){
            Log.node.error("{} can't find receiver, method:{}", _name, method);
            return null;
        }
        return dest;
    }

    //TODO create a LobbyManager
    @Deprecated
    public int queryWorkerId(String workerName){
        List<String> workers = new ArrayList<>();
        for (var n : _call_dest.keySet()) {
            if (n.startsWith(workerName))
                workers.add(n);
        }

        int r = Utils.nextInt(0, workers.size() - 1);
        String sessionId = workers.get(r).split("&")[0].split("\\$")[1];
        return Integer.parseInt(sessionId);
    }

    //use config to load message map and distr map in the future
    public IConstMessage getMessageMap() { return messageMap_; }

    public void setMessageMap(IConstMessage messageMap) { messageMap_ = messageMap; }

    public IConstDistrClass getDistrMap() { return distrMap_; }

    public void setDistrMap(IConstDistrClass distrMap) { distrMap_ = distrMap; }

    public String getName(){
        return _name;
    }
}
