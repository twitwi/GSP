# - Try to find the OMiSCID library
# Specify one or more of the following components
# as you call this find module. See example below.
#
#   system
#   com
#   control
#   messaging
#
# Once done this will define
#
#  OMISCID_FOUND - System has Omiscid
#  OMISCID_INCLUDE_DIR - The Omiscid include directory
#  OMISCID_LIBRARIES - The libraries needed to use Omiscid

# Copyright (c) 2010 INRIA. All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions
# are met:
# 1. Redistributions of source code must retain the above copyright
#    notice, this list of conditions and the following disclaimer.
# 2. Redistributions in binary form must reproduce the above copyright
#    notice, this list of conditions and the following disclaimer in the
#    documentation and/or other materials provided with the distribution.
#
# THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
# ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
# FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
# DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
# OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
# HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
# LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
# OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
# SUCH DAMAGE.
#
# The views and conclusions contained in the software and documentation are
# those of the authors and should not be interpreted as representing
# official policies, either expressed or implied, of INRIA.


IF(OMISCID_LIBRARIES)
   SET(Omiscid_FIND_QUIETLY TRUE)
ENDIF(OMISCID_LIBRARIES)

IF(NOT Omiscid_FIND_COMPONENTS)
   # Assume they only want control
   SET(Omiscid_FIND_COMPONENTS control com system messaging)
ENDIF(NOT Omiscid_FIND_COMPONENTS)

SET(OMISCID_INCLUDE_DIR)
SET(OMISCID_LIBRARIES)
SET(OMISCID_FOUND TRUE)
FOREACH(COMPONENT ${Omiscid_FIND_COMPONENTS})
   STRING(TOUPPER ${COMPONENT} COMPONENT_UPPERCASE)

   IF(COMPONENT STREQUAL "control")
     FIND_PATH(OMISCID-CONTROL_INCLUDE_DIR
       ServiceControl/VariableAttribute.h
       ServiceControl/ServiceProperties.h
       ServiceControl/Factory.h
       ServiceControl/LocalVariableListener.h
       ServiceControl/ControlServer.h
       ServiceControl/ServiceProxy.h
       ServiceControl/DnsSdService.h
       ServiceControl/VariableAttributeListener.h
       ServiceControl/Service.h
       ServiceControl/BrowseForDnsSdService.h
       ServiceControl/ControlUtils.h
       ServiceControl/ServicesCommon.h
       ServiceControl/ServiceFromXML.h
       ServiceControl/XsdSchema.h
       ServiceControl/ServiceProxyList.h
       ServiceControl/ServicesTools.h
       ServiceControl/ControlClient.h
       ServiceControl/WaitForDnsSdServices.h
       ServiceControl/ConfigServiceControl.h
       ServiceControl/DnsSdProxy.h
       ServiceControl/RemoteVariableChangeListener.h
       ServiceControl/ConnectorListener.h
       ServiceControl/XMLTreeParser.h
       ServiceControl/ServiceRepositoryListener.h
       ServiceControl/XsdValidator.h
       ServiceControl/ServiceFilter.h
       ServiceControl/InOutputAttribute.h
       ServiceControl/StringVariableAttribute.h
       ServiceControl/IntVariableAttribute.h
       ServiceControl/ServiceRepository.h
       ServiceControl/Attribute.h
       ServiceControl/UserFriendlyAPI.h
       PATHS
       PATH_SUFFIXES Omiscid)
     FIND_LIBRARY(OMISCID-CONTROL_LIBRARIES NAMES OmiscidControl)

     IF(OMISCID-CONTROL_INCLUDE_DIR AND OMISCID-CONTROL_LIBRARIES)
       SET(OMISCID_INCLUDE_DIR ${OMISCID-CONTROL_INCLUDE_DIR} ${OMISCID_INCLUDE_DIR})
       SET(OMISCID_LIBRARIES ${OMISCID-CONTROL_LIBRARIES} ${OMISCID_LIBRARIES})
       SET(OMISCID-CONTROL_FOUND TRUE)
     ELSE(OMISCID-CONTROL_INCLUDE_DIR AND OMISCID-CONTROL_LIBRARIES)
       SET(OMISCID-CONTROL_FOUND FALSE)
     ENDIF(OMISCID-CONTROL_INCLUDE_DIR AND OMISCID-CONTROL_LIBRARIES)
     
     MARK_AS_ADVANCED(OMISCID-CONTROL_INCLUDE_DIR OMISCID-CONTROL_LIBRARIES)
   ELSEIF(COMPONENT STREQUAL "com")
     FIND_PATH(OMISCID-COM_INCLUDE_DIR
       Com/ComTools.h
       Com/Connector.h
       Com/MsgManager.h
       Com/MsgSocket.h
       Com/TcpServer.h
       Com/ConfigCom.h
       Com/Message.h
       Com/MsgSocketException.h
       Com/TcpClient.h
       Com/UdpExchange.h
       PATHS
       PATH_SUFFIXES Omiscid)
     FIND_LIBRARY(OMISCID-COM_LIBRARIES NAMES OmiscidCom)

     IF(OMISCID-COM_INCLUDE_DIR AND OMISCID-COM_LIBRARIES)
       SET(OMISCID_INCLUDE_DIR ${OMISCID-COM_INCLUDE_DIR} ${OMISCID_INCLUDE_DIR})
       SET(OMISCID_LIBRARIES ${OMISCID-COM_LIBRARIES} ${OMISCID_LIBRARIES})
       SET(OMISCID-COM_FOUND TRUE)
     ELSE(OMISCID-COM_INCLUDE_DIR AND OMISCID-COM_LIBRARIES)
       SET(OMISCID-COM_FOUND FALSE)
     ENDIF(OMISCID-COM_INCLUDE_DIR AND OMISCID-COM_LIBRARIES)
     
     MARK_AS_ADVANCED(OMISCID-COM_INCLUDE_DIR OMISCID-COM_LIBRARIES)
     
   ELSEIF(COMPONENT STREQUAL "system")
     FIND_PATH(OMISCID-SYSTEM_INCLUDE_DIR
       System/AtomicCounter.h
       System/AtomicReentrantCounter.h
       System/AutoDelete.h
       System/ConfigSystem.h
       System/DateAndTime.h
       System/ElapsedTime.h
       System/Event.h
       System/LockManagement.h
       System/MultipleReferencedData.h
       System/MutexedSimpleList.h
       System/Mutex.h
       System/Portage.h
       System/RecycleSimpleList.h
       System/ReentrantMutex.h
       System/SharedMemSegment.h
       System/SimpleException.h
       System/SimpleListException.h
       System/SimpleList.h
       System/SimpleString.h
       System/SocketException.h
       System/Socket.h
       System/TemporaryMemoryBuffer.h
       System/Thread.h
       System/TrackingMemoryLeaks.h
       PATHS
       PATH_SUFFIXES Omiscid)
     FIND_LIBRARY(OMISCID-SYSTEM_LIBRARIES NAMES OmiscidSystem)

     IF(OMISCID-SYSTEM_INCLUDE_DIR AND OMISCID-SYSTEM_LIBRARIES)
       SET(OMISCID_INCLUDE_DIR ${OMISCID-SYSTEM_INCLUDE_DIR} ${OMISCID_INCLUDE_DIR})
       SET(OMISCID_LIBRARIES ${OMISCID-SYSTEM_LIBRARIES} ${OMISCID_LIBRARIES})
       SET(OMISCID-SYSTEM_FOUND TRUE)
     ELSE(OMISCID-SYSTEM_INCLUDE_DIR AND OMISCID-SYSTEM_LIBRARIES)
       SET(OMISCID-SYSTEM_FOUND FALSE)
     ENDIF(OMISCID-SYSTEM_INCLUDE_DIR AND OMISCID-SYSTEM_LIBRARIES)
     
     MARK_AS_ADVANCED(OMISCID-SYSTEM_INCLUDE_DIR OMISCID-SYSTEM_LIBRARIES)

   ELSEIF(COMPONENT STREQUAL "messaging")
     FIND_PATH(OMISCID-MESSAGING_INCLUDE_DIR
       Json/json_spirit.h
       Json/json_spirit_writer.h
       Json/json_spirit_reader.h
       Json/json_spirit_value.h
       Messaging/StructuredMethodCallbackFactory.h
       Messaging/StructuredResult.h
       Messaging/Json.h
       Messaging/Access.h
       Messaging/StructuredMethodCallbackListener.h
       Messaging/MethodCallback.h
       Messaging/DelayedResult.h
       Messaging/MethodCallbackFactory.h
       Messaging/StructuredMessageException.h
       Messaging/StructuredMethodCallback_NP.h
       Messaging/StructuredMethodCall.h
       Messaging/StructuredMessage.h
       Messaging/StructuredMethodCallback.h
       Messaging/StructuredParameters.h
       Messaging/Messaging.h
       PATHS
       PATH_SUFFIXES Omiscid)
     
     FIND_LIBRARY(OMISCID-MESSAGING_LIBRARY NAMES OmiscidMessaging )
     FIND_LIBRARY(OMISCID-JSON_LIBRARY NAMES JsonSpirit)
     MARK_AS_ADVANCED(OMISCID-MESSAGING_LIBRARY OMISCID-JSON_LIBRARY)
     SET(OMISCID-MESSAGING_LIBRARIES ${OMISCID-MESSAGING_LIBRARY} ${OMISCID-JSON_LIBRARY})
     
     IF(OMISCID-MESSAGING_INCLUDE_DIR AND OMISCID-MESSAGING_LIBRARIES)
       SET(OMISCID_INCLUDE_DIR ${OMISCID-MESSAGING_INCLUDE_DIR} ${OMISCID_INCLUDE_DIR})
       SET(OMISCID_LIBRARIES ${OMISCID-MESSAGING_LIBRARIES} ${OMISCID_LIBRARIES})
       SET(OMISCID-MESSAGING_FOUND TRUE)
     ELSE(OMISCID-MESSAGING_INCLUDE_DIR AND OMISCID-MESSAGING_LIBRARIES)
       SET(OMISCID-MESSAGING_FOUND FALSE)
     ENDIF(OMISCID-MESSAGING_INCLUDE_DIR AND OMISCID-MESSAGING_LIBRARIES)
     
     MARK_AS_ADVANCED(OMISCID-MESSAGING_INCLUDE_DIR OMISCID-MESSAGING_LIBRARIES)

   ENDIF()
ENDFOREACH(COMPONENT ${Omiscid_FIND_COMPONENTS})

LIST(REMOVE_DUPLICATES OMISCID_INCLUDE_DIR)
LIST(REMOVE_DUPLICATES OMISCID_LIBRARIES)

SET(OMISCID_INCLUDE_DIR ${OMISCID_INCLUDE_DIR} CACHE PATH "")
SET(OMISCID_LIBRARIES ${OMISCID_LIBRARIES} CACHE PATH "")

IF(OMISCID_FOUND)
   IF(NOT Omiscid_FIND_QUIETLY)
      MESSAGE(STATUS "Found Omiscid: ${OMISCID_LIBRARIES}")
   ENDIF(NOT Omiscid_FIND_QUIETLY)
ELSE(OMISCID_FOUND)
   IF(Omiscid_FIND_REQUIRED)
      MESSAGE(FATAL_ERROR "Could NOT find Omiscid")
   ENDIF(Omiscid_FIND_REQUIRED)
ENDIF(OMISCID_FOUND)

MARK_AS_ADVANCED(OMISCID_INCLUDE_DIR OMISCID_LIBRARIES)

