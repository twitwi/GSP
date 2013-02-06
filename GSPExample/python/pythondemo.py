



class Div:
    def __init__(self):
        self.enabled = True # example with bool
        self.divisor = 1. # to type it as double

    # a "gsp(...)" function will be injected
    # TODO onDivisorChanged(oldVal, newVal) ??

    def input(self, value):
        if not(self.enabled):
            return
        res = value / self.divisor
        self.gsp("output", res)
        self.gsp("string", str(res))


class Log:
    def __init__(self):
        print("Log class: creating instance")
        self.hello = ""

    def helloChanged(self, old, new):
        print("Log class: hello value has been changed from "+str(old)+" to "+str(new)+" and is currently "+str(self.hello))

    def input(self, message):
        #print(type(self.hello), self.hello)
        print("Log class: message: "+str(message))

    def highlight(self, message):
        print("Log class: MESSAGE: "+str(message))

    def initModule(self):
        print("Log class: receiving initModule callback, hello is '"+self.hello+"'")

    def stopModule(self):
        print("Log class: receiving stopModule callback")
