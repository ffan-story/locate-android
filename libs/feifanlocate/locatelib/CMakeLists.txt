
# 设置构建本地库需要的CMake最小版本
cmake_minimum_required(VERSION 3.4.1)

# 指定本地库名称、类型（动态库SHARED或静态库STATIC）
# 提供源代码的相对路径
# 可以使用add_library()添加多个库
# Gradle自动将共享库打包到APK中
#add_library( locate
#             SHARED
#             src/main/cpp/model.cpp
#             src/main/cpp/euclid-distance-estimator.cpp
#             src/main/cpp/position-estimator.cpp)

# 指定头文件目录
include_directories(src/main/cpp/include/)