package morgan.db;

import morgan.structure.Node;
import morgan.structure.Worker;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DBStartUp {

    public static void main(String[] args) {
        Node node = new Node("Conn", "tcp://127.0.0.1:3340");
        Worker dbcenter = new DBCenter(node, "DBCenter");
        node.addWorker(dbcenter);
        node.startUp();
        dbcenter.schdule(1000, () -> {
            dbcenter.Call("remove", "DBWorker1", "test_two", 1003);
            dbcenter.Call("remove", "DBWorker1", "test_two", 1004);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", 1003);
            item.put("field_o", "second test");
            item.put("field_t", 9.36d);
            item.put("field_th", 50);
            item.put("field_f", 3.56f);
            dbcenter.Call("insert", "DBWorker1", "test_two", new LinkedHashMap<>(item));
            Map<String, Object> item2 = new LinkedHashMap<>();
            item2.put("id", 1004);
            item2.put("field_o", "insert test1004");
            item2.put("field_t", 9.995d);
            item2.put("field_th", 85);
            item2.put("field_f", 15.95f);
            dbcenter.Call("insert", "DBWorker1", "test_two", new LinkedHashMap<>(item2));
            item.remove("id");
            item.put("field_o", "update test");
            item.put("field_t", 99.9d);
            item.put("field_th", 5);
            item.put("field_f", 3.1f);
            dbcenter.Call("update", "DBWorker1", "test_two", 1003, new ArrayList<>(item.keySet()), new ArrayList<>(item.values()));
            item.put("field_o", "update test2");
            item.remove("field_t");
            item.remove("field_th");
            item.put("field_f", 98.7f);
            dbcenter.Call("update", "DBWorker1", "test_two", 1003, new ArrayList<>(item.keySet()), new ArrayList<>(item.values()));
            dbcenter.Call("selectById", "DBWorker1", "test_two", 1003);
            dbcenter.Listen((r) -> {
                System.out.println("listen one:");
                List<Record> records = r.getResult("result");
                for (var re : records) {
                    System.out.println(re);
                }
            });
            dbcenter.Call("selectByBinds", "DBWorker1", "test_two", "field_o", "insert test1004");
            dbcenter.Listen((r) -> {
                System.out.println("listen two:");
                List<Record> records = r.getResult("result");
                for (var re : records) {
                    System.out.println(re);
                }
            });
        });
    }
}
