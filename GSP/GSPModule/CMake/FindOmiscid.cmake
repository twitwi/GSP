# - a Omiscid module for CMake
#
# Try to find omiscid and define the following variables:
#
#   Omiscid_FOUND
#   Omiscid_LIBRARIES
#   Omiscid_LIBRARY_DIRS
#   Omiscid_INCLUDE_DIR
#   Omiscid_CFLAGS

find_program( Omiscid_CONFIG_EXECUTABLE OmiscidControl-config )
mark_as_advanced( Omiscid_CONFIG_EXECUTABLE )
  
message( "Omiscid executable : ${Omiscid_CONFIG_EXECUTABLE}" )

if( Omiscid_CONFIG_EXECUTABLE )
  set( Omiscid_FOUND 1 )
endif( Omiscid_CONFIG_EXECUTABLE )

#set Omiscid_LIBRARIES Variable
execute_process( COMMAND ${Omiscid_CONFIG_EXECUTABLE} "--libs-only-l"
  OUTPUT_VARIABLE Omiscid_LIBRARIES)
string(REGEX REPLACE "[\r\n]" " " Omiscid_LIBRARIES "${Omiscid_LIBRARIES}")
string(REGEX REPLACE " -l" " " Omiscid_LIBRARIES "${Omiscid_LIBRARIES}")
string(REGEX REPLACE " +" " " Omiscid_LIBRARIES "${Omiscid_LIBRARIES}")
string(REGEX REPLACE "[ \t]+$" "" Omiscid_LIBRARIES "${Omiscid_LIBRARIES}")
string(REGEX REPLACE "^[ \t]+" "" Omiscid_LIBRARIES "${Omiscid_LIBRARIES}")
separate_arguments( Omiscid_LIBRARIES )

#set Omiscid_LIBRARY_DIRS Variable
execute_process( COMMAND ${Omiscid_CONFIG_EXECUTABLE} "--libs-only-L"
  OUTPUT_VARIABLE Omiscid_LIBRARY_DIRS)
string(REGEX REPLACE "[\r\n]" " " Omiscid_LIBRARY_DIRS "${Omiscid_LIBRARY_DIRS}")
string(REGEX REPLACE " -L" " " Omiscid_LIBRARY_DIRS "${Omiscid_LIBRARY_DIRS}")
string(REGEX REPLACE " +" " " Omiscid_LIBRARY_DIRS "${Omiscid_LIBRARY_DIRS}")
string(REGEX REPLACE "[ \t]+$" "" Omiscid_LIBRARY_DIRS "${Omiscid_LIBRARY_DIRS}")
string(REGEX REPLACE "^[ \t]+" "" Omiscid_LIBRARY_DIRS "${Omiscid_LIBRARY_DIRS}")
separate_arguments( Omiscid_LIBRARY_DIRS )

#set Omiscid_INCLUDE_DIR Variable
execute_process( COMMAND ${Omiscid_CONFIG_EXECUTABLE} "--cflags"
  OUTPUT_VARIABLE Omiscid_INCLUDE_DIR)
string(REGEX REPLACE "[\r\n]" " " Omiscid_INCLUDE_DIR "${Omiscid_INCLUDE_DIR}")
string(REGEX MATCHALL "-I[^ \t]*" Omiscid_INCLUDE_DIR "${Omiscid_INCLUDE_DIR}")
string(REPLACE "-I" " " Omiscid_INCLUDE_DIR "${Omiscid_INCLUDE_DIR}")
string(REGEX REPLACE " +" " " Omiscid_INCLUDE_DIR "${Omiscid_INCLUDE_DIR}")
string(REGEX REPLACE "[ \t]+$" "" Omiscid_INCLUDE_DIR "${Omiscid_INCLUDE_DIR}")
string(REGEX REPLACE "^[ \t]+" "" Omiscid_INCLUDE_DIR "${Omiscid_INCLUDE_DIR}")
separate_arguments( Omiscid_INCLUDE_DIR )

execute_process( COMMAND ${Omiscid_CONFIG_EXECUTABLE} "--cflags"
  OUTPUT_VARIABLE Omiscid_CFLAGS)


if( Omiscid_FOUND )
else( Omiscid_FOUND )
  if( Omiscid_FOUND_REQUIRED )
	message(SEND_ERROR "Unable to find Omiscid")
  endif( Omiscid_FOUND_REQUIRED )
endif( Omiscid_FOUND )

message( "Omiscid include directories : ${Omiscid_INCLUDE_DIRS}" )
