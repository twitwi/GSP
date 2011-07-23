project ( GSPExample-cpp )

cmake_minimum_required( VERSION 2.6 )

add_library( NativeCppDemo SHARED NativeCppDemo )
add_library( AdvancedDemo SHARED AdvancedDemo Subclassing)
