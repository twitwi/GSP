



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
        self.gsp("output", res)
        self.gsp("string", str(res))


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
