
from ctypes import *

# idea, auto import if finds a type with the same name??? here would be CustomType
class CustomType(Structure):
    _fields_ = [("score", c_float),
                ("x", c_int),
                ("y", c_int)]


class ReceiveCustom:
    def input(self, cc):
        c = cc if isinstance(cc, CustomType) else CustomType.from_address(cc)
        print("Py-eived: %d,%d \t-> %f" % (c.x, c.y, c.score))

    def inputArray(self, count, cc):
        values = (CustomType*count).from_address(cc)
        print("Py-eived array of size %d" % (count,));
        for i in range(count):
            print("  values[%d]: %d,%d \t-> %f" % (i, values[i].x, values[i].y, values[i].score));

class EmitCustom:
    def input(self, i):
        c = CustomType()
        c.x = 100 + i
        c.y = 100 + i
        if i%3 == 0:
            c.score = 1.
        else:
            c.score = 0.
        # in this direction, we will need conversion to C++ mangled type... so we need a type name (here not Custom but CustomType (the c one)) => convention to use the same + return a ref containing the type also... byref() seem to be not bad, but  how to access info...
        # OK::::: should send c (as done) but the getCView should see if it is a ctypes.Structure isinstance(c, cytpes.Structure) (but in java) and then find a way to get the address (addressof) and the type   type(c).__name__    allowing override in the classe e.g., c.cClassName  and some escape for namespaces (e.g., c.cClassName = "Heeere__v__CustomType"
        # maybe need to make a small python code to help in writting the java factory (use as less as possible the python-c api
        self.emitNamedEvent("output", c)
            #self.emitNamedEvent("output", byref(c))
