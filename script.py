import threading
import multiprocessing
import subprocess
import sys

MAX_WORKERS = 2
NO_RUNS = 200
INITIAL_NO_RUNS = 200

#Count lines in a folder
def bufcount(filename):
    f = open(filename)                  
    lines = 0
    buf_size = 1024 * 1024
    read_f = f.read # loop optimization

    buf = read_f(buf_size)
    while buf:
        lines += buf.count('\n')
        buf = read_f(buf_size)

    return lines

# Print iterations progress
def printProgressBar (iteration, total, prefix = '', suffix = '', decimals = 1, length = 100, fill = '█', printEnd = "\r"):
    """
    Call in a loop to create terminal progress bar
    @params:
        iteration   - Required  : current iteration (Int)
        total       - Required  : total iterations (Int)
        prefix      - Optional  : prefix string (Str)
        suffix      - Optional  : suffix string (Str)
        decimals    - Optional  : positive number of decimals in percent complete (Int)
        length      - Optional  : character length of bar (Int)
        fill        - Optional  : bar fill character (Str)
        printEnd    - Optional  : end character (e.g. "\r", "\r\n") (Str)
    """
    percent = ("{0:." + str(decimals) + "f}").format(100 * (iteration / float(total)))
    filledLength = int(length * iteration // total)
    bar = fill * filledLength + '-' * (length - filledLength)
    print('\r%s |%s| %s%% %s' % (prefix, bar, percent, f'{suffix} {bufcount("./logs/GameData.txt")}'), end = printEnd)
    # Print New Line on Complete
    if iteration == total:
        print()


def popenAndCall(onExit, popenArgs):
    """
    Runs the given args in a subprocess.Popen, and then calls the function
    onExit when the subprocess completes.
    onExit is a callable object, and popenArgs is a list/tuple of args that
    would give to subprocess.Popen.
    """
    def runInThread(onExit, command):
        proc = subprocess.Popen(command, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
        proc.wait()
        onExit()
        return
    thread = threading.Thread(target=runInThread, args=(onExit, popenArgs))
    thread.start()
    # returns immediately after the thread starts
    return thread

def freeMutex():
    global NO_RUNS
    global MAX_WORKERS
    MAX_WORKERS += 1
    NO_RUNS -= 1
    printProgressBar(INITIAL_NO_RUNS - NO_RUNS, INITIAL_NO_RUNS + MAX_WORKERS, prefix = 'Progress:', suffix = 'Complete', length = 100)


def main():
    global NO_RUNS
    global MAX_WORKERS

    command = r'"C:\Program Files\JetBrains\IntelliJ IDEA 2019.2.3\jbr\bin\java.exe" "-javaagent:C:\Program Files\JetBrains\IntelliJ IDEA 2019.2.3\lib\idea_rt.jar=55408:C:\Program Files\JetBrains\IntelliJ IDEA 2019.2.3\bin" -Dfile.encoding=UTF-8 -classpath C:\Users\gonca\IdeaProjects\AIAD\target\classes;C:\Users\gonca\.m2\repository\com\tilab\jade\jade\4.5.0\jade-4.5.0.jar;C:\Users\gonca\.m2\repository\commons-codec\commons-codec\1.13\commons-codec-1.13.jar;C:\Users\gonca\.m2\repository\org\json\json\20190722\json-20190722.jar;C:\Users\gonca\.m2\repository\com\google\code\gson\gson\2.8.6\gson-2.8.6.jar;C:\Users\gonca\.m2\repository\commons-io\commons-io\2.6\commons-io-2.6.jar;C:\Users\gonca\.m2\repository\commons-cli\commons-cli\1.4\commons-cli-1.4.jar Main"'

    printProgressBar(INITIAL_NO_RUNS - NO_RUNS, INITIAL_NO_RUNS + MAX_WORKERS, prefix = 'Progress:', suffix = 'Complete', length = 100)
    while NO_RUNS > 0:
        if (MAX_WORKERS > 0):
            popenAndCall(freeMutex, command)
            MAX_WORKERS -= 1
            printProgressBar(INITIAL_NO_RUNS - NO_RUNS, INITIAL_NO_RUNS + MAX_WORKERS, prefix = 'Progress:', suffix = 'Complete', length = 100)

    print("program continued")
    sys.exit(0)


while True:
   main()
   time.sleep(30) # 3600 seconds = 1 hour
   NO_RUNS = 200
   INITIAL_NO_RUNS = 200


sys.exit(0)