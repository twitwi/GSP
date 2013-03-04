
import ctypes as gsp__ctypes
from ast import literal_eval as make_tuple

class gsp__:
    def isStructureOrArray(self, o):
        return isinstance(o, gsp__ctypes.Structure) or isinstance(o, gsp__ctypes.Array)
    def cAddress(self, o):
        return gsp__ctypes.addressof(o)
    def cClassName(self, o):
        t = type(o)
        if isinstance(o, gsp__ctypes.Array):
            t = t._type_
        if hasattr(t,"cClassName"):
            n = t.cClassName
        else:
            n = type(t).__name__
        return n
    def typeString(self, o):
        return type(o).__name__
    def valueString(self, o):
        return str(o)
    def isString(self, o):
        return type(o) == str
    def makeTuple(self, s):
        return make_tuple(s)

GSP = gsp__()
