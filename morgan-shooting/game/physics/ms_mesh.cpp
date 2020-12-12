#include <fstream>
#include <string.h>
#include <sstream>
#include <memory.h>
#include <vector>
#include "ms_mesh.h"

namespace msgame
{
    namespace physics
    {
        Mesh playerMesh_;
        
        Mesh LoadFromObj(std::string path)
        {
            Mesh mesh;
            std::ifstream ifs;
            ifs.open(path);
            char buf[512];
            if (ifs.is_open())
            {
                std::vector<msutils::vector::Vector3> vertexs;
                int32 vtxIndex = 0;
                while (ifs.good() && !ifs.eof())
                {
                    memset(buf, 0, 512);
                    ifs.getline(buf, 512);

                    if (buf[0] == 'v' && buf[1] == ' ')
                    {
                        float cords[3];
                        int32 start = 2;
                        for (int32 j = 0; j < 3; j++)
                        {
                            int32 index = start;
                            int32 size = 0;
                            while (buf[index] != ' ' && buf[index] != '\0')
                            {
                                index++;
                                size++;
                            }
                            char * ptr = new char[size];
                            memcpy(ptr, buf + start, size);
                            std::istringstream stream(ptr);
                            stream >> cords[j];
                            start = index + 1;
                            delete ptr;
                        }
                        msutils::vector::Vector3 v(cords[0], cords[1], cords[2]);
                        vertexs.push_back(v);
                    }

                    if (buf[0] == 'v' && buf[1] == 'n')
                    {
                        float cords[3];
                        int32 start = 3;
                        for (int32 j = 0; j < 3; j++)
                        {
                            int32 index = start;
                            int32 size = 0;
                            while (buf[index] != ' ' && buf[index] != '\0')
                            {
                                index++;
                                size++;
                            }
                            char * ptr = new char[size];
                            memcpy(ptr, buf + start, size);
                            std::istringstream stream(ptr);
                            stream >> cords[j];
                            start = index + 1;
                            delete ptr;
                        }
                        msutils::vector::Vector3 normal(cords[0], cords[1], cords[2]);
                        mesh.AddVertex(vertexs[vtxIndex], normal);
                        vtxIndex++;
                    }

                    if (buf[0] == 'f')
                    {
                        int32 indexs[3];
                        int32 start = 2;
                        for (int32 j = 0; j < 3; j++)
                        {
                            int32 index = start;
                            int32 size = 0;
                            while (buf[index] != '/' && buf[index] != '\0')
                            {
                                index++;
                                size++;
                            }
                            char * ptr = new char[size];
                            memcpy(ptr, buf + start, size);
                            std::istringstream stream(ptr);
                            stream >> indexs[j];
                            start = index + size * 2 + 3;
                            delete ptr;
                        }
                        mesh.AddTriangle(indexs[0], indexs[1], indexs[2]);
                    }
                }
                ifs.close();
            }
            playerMesh_ = mesh;
            return mesh;
        }

        void Mesh::AddVertex(msutils::vector::Vector3 cord, msutils::vector::Vector3 normal)
        {
            Vertex vtx(cord, normal);
            vertexs_.push_back(vtx);
        }

        void Mesh::AddTriangle(int32 index1, int32 index2, int32 index3)
        {
            Triangle t(index1, index2, index3);
            triangles_.push_back(t);
        }

        void Mesh::SortVertexs()
        {
            for (int32 i = 0; i < 8; i++)
            {
                std::vector<int32> octant;
                sortedVertex_.push_back(octant);
            }

            for (int32 i = 0; i < vertexs_.size(); i++)
            {
                auto v = vertexs_[i];
                sortedVertex_[GetOctant(v.cord_) - 1].push_back(i);
            }
        }

        inline int32 Mesh::GetOctant(msutils::vector::Vector3 cord)
        {
            if (cord.x >= 0.00000f && cord.y > 0.00000f && cord.z > 0.00000f)
            {
                return 1;
            }
            else if (cord.x < 0.00000f && cord.y > 0.00000f && cord.z > 0.00000f)
            {
                return 2;
            }
            else if (cord.x < 0.00000f && cord.y > 0.00000f && cord.z <= 0.00000f)
            {
                return 3;
            }
            else if (cord.x >= 0.00000f && cord.y > 0.00000f && cord.z <= 0.00000f)
            {
                return 4;
            }
            else if (cord.x >= 0.00000f && cord.y <= 0.00000f && cord.z > 0.00000f)
            {
                return 5;
            }
            else if (cord.x < 0.00000f && cord.y <= 0.00000f && cord.z > 0.00000f)
            {
                return 6;
            }
            else if (cord.x < 0.00000f && cord.y <= 0.00000f && cord.z <= 0.00000f)
            {
                return 7;
            }
            else if (cord.x >= 0.00000f && cord.y <= 0.00000f && cord.z <= 0.00000f)
            {
                return 8;
            }
            
            return 0;
        }

        inline std::vector<int32> Mesh::GetSortedVertex(int32 octant)
        {
            return sortedVertex_[octant - 1];
        }
    } // namespace physics
} // namespace msgame
