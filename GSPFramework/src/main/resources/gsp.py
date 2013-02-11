
import ctypes as gsp__ctypes

class gsp__:
    def isStructure(self, o):
        return isinstance(o, gsp__ctypes.Structure)
    def cAddress(self, o):
        return gsp__ctypes.addressof(o)
    def cClassName(self, o):
        if hasattr(o,"cClassName"):
            n = o.cClassName
        else:
            n = type(o).__name__
        return n

GSP = gsp__()
