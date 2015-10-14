
class Semaphore {
    private int init;

    public Semaphore(int init) {
        this.init = init;
    }

    synchronized void release() {
        while (init == 0)
            try {
                wait();
            } catch (InterruptedException e) {
            }
        init = 0;
    }

    synchronized void take() {
        init = 1;
        this.notify();
    }
}
