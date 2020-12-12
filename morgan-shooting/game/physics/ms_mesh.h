#ifndef _ms_mesh_
#define _ms_mesh_

#include <vector>
#include <string>
#include "ms_types.h"
#include "ms_vector3.h"

#define PHYSICS_PREFIX msgame::physics

namespace msgame
{
    namespace physics
    {
        struct Triangle
        {
            public:
                int32 VIndex1_;
                int32 VIndex2_;
                int32 VIndex3_;
                Triangle(int32 i1, int32 i2, int32 i3) : VIndex1_(i1), VIndex2_(i2), VIndex3_(i3) {}
        };

        struct Vertex
        {
            public:
                msutils::vector::Vector3 cord_;
                msutils::vector::Vector3 normal_;
                Vertex(msutils::vector::Vector3 cord, msutils::vector::Vector3 normal) : cord_(cord), normal_(normal) {}
        };
        
        //Mesh is Deprecated!!!!!!!
        class Mesh
        {
        public:
            std::vector<Vertex> vertexs_;
            std::vector<Triangle> triangles_;
            std::vector<std::vector<int32>> sortedVertex_;

            void AddVertex(msutils::vector::Vector3 cord, msutils::vector::Vector3 normal);
            void AddTriangle(int32 index1, int32 index2, int32 index3);
            void SortVertexs();
            int32 GetOctant(msutils::vector::Vector3 cord);
            std::vector<int32> GetSortedVertex(int32 octant);

            Mesh() {}
            virtual ~Mesh() {}
        };

        extern Mesh LoadFromObj(std::string path);
        extern Mesh playerMesh_;
    }
}

#endif