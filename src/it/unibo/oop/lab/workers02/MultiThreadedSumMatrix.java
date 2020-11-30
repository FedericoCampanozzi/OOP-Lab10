package it.unibo.oop.lab.workers02;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadedSumMatrix implements SumMatrix {

    private final int nthread;

    MultiThreadedSumMatrix(final int nthread) {
        this.nthread = nthread;
    }

    @Override
    public double sum(final double[][] matrix) {
        final int total = matrix.length * matrix[0].length;
        final int size = total % nthread + total / nthread;

        final List<Worker> workers = new ArrayList<>(nthread);
        for (int i = 0; i < total; i += size) {
            workers.add(new Worker(matrix, i, size));
        }

        for (final Worker w: workers) {
            w.start();
        }

        long sum = 0;

        for (final Worker w: workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }

        return sum;
    }

    private static class Worker extends Thread {
        private final double[][] matrix;
        private int offset;
        private final int nelem;
        private long res;

        /**
         * Build a new worker.
         * 
         * @param list
         *            the list to sum
         * @param startpos
         *            the initial position for this worker
         * @param nelem
         *            the no. of elems to sum up for this worker
         */
        Worker(final double[][] matrix, final int offset, final int nelem) {
            super(Integer.toString(nelem));
            this.matrix = matrix;
            this.offset = offset;
            this.nelem = nelem;
        }

        @Override
        public void run() {
            //System.out.println("start index as list : " + this.offset + "");
            for (int i = 0; i < this.nelem; i++) {
                this.res += this.matrix[(this.offset + i) % matrix.length][(this.offset + i) / matrix.length];
            }
        }

        /**
         * Returns the result of summing up the integers within the list.
         * 
         * @return the sum of every element in the array
         */
        public long getResult() {
            return this.res;
        }

    }


}
