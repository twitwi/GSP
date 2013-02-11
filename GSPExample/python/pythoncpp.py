
from ctypes import *

class Custom(Structure):
    cClassName = "CustomType" # optionnal, by default would take the class name ("Custom" here)
    _fields_ = [("score", c_float),
                ("x", c_int),
                ("y", c_int)]

class ReceiveCustom:
    def input(self, cc):
        c = cc if isinstance(cc, Custom) else Custom.from_address(cc) # accepts both python object and c address
        print("Py-eived: %d,%d \t-> %f" % (c.x, c.y, c.score))

    def inputArray(self, count, cc):
        values = (Custom*count).from_address(cc)
        print("Py-eived array of size %d" % (count,));
        for i in range(count):
            print("  values[%d]: %d,%d \t-> %f" % (i, values[i].x, values[i].y, values[i].score));

class EmitCustom:
    def input(self, i):
        c = Custom()
        c.x = 100 + i
        c.y = 100 + i
        if i%3 == 0:
            c.score = 1.
        else:
            c.score = 0.
        self.emitNamedEvent("output", c)





class NSType(Structure):
    cClassName = "heeere::Type"
    _fields_ = [("x", c_int),
                ("y", c_int)]

class NSModule:
    def input(self, i):
        c = NSType()
        c.x = 100 + i
        c.y = 100 + i
        self.emitNamedEvent("output", c)

    def o(self, cc):
        c = cc if isinstance(cc, NSType) else NSType.from_address(cc)
        print("Py-eived: %d,%d" % (c.x, c.y))

