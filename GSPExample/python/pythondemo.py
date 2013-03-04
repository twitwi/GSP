



class Div:
    def __init__(self):
        self.enabled = True # example with bool
        self.divisor = 1. # to type it as double

    # a "gsp(...)" function will be injected
    
    def divisorChanged(self, oldVal, newVal):
        if newVal == 0.:
            self.divisor = oldVal

    def input(self, value):
        if not(self.enabled):
            return
        res = value / self.divisor
        self.emitNamedEvent("output", res)
        self.emitNamedEvent("string", str(res))


class Log:
    def __init__(self):
        print("Log class: creating instance")
        self.hello = "world"

    def helloChanged(self, old, new):
        print("Log class: hello value has been changed from '"+str(old)+"' to '"+str(new)+"' and is currently '"+self.hello+"'")

    def input(self, message):
        #print(type(self.hello), self.hello)
        print("Log class: message: "+str(message))

    def highlight(self, message):
        print("Log class: MESSAGE: "+str(message))

    def initModule(self):
        print("Log class: receiving initModule callback, hello is '"+self.hello+"'")

    def stopModule(self):
        print("Log class: receiving stopModule callback")

class Parameters:
    pint = 0
    pfloat = 0.
    pstr = "hello"
    ptuple = (255,0,0)
    def input(self, i):
        print "pint:", self.pint
        print "pfloat:", self.pfloat
        print "pstr:", self.pstr
        print "ptuple:", self.ptuple
        
