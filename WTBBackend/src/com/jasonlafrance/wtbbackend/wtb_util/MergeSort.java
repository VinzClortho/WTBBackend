package com.jasonlafrance.wtbbackend.wtb_util;

/**
 *
 * @author Jason LaFrance
 */
public class MergeSort {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int[] list = new int[50];

        for (int i = 0; i < list.length; i++) {
            list[i] = (int) (Math.random() * 100);
        }

        showList(list);
        mergeSort(list);
        showList(list);

    }

    public static void showList(int[] list) {
        for (int e : list) {
            System.out.print(e + " ");
        }
        System.out.println("\n");
    }

    public static void mergeSort(int[] list) {
        // create a temp array with is the same size as the original for
        // scratch space.  initializing it here and passing it into the 
        // recursion will cut out a lot of memory allocations.  Also, since
        // each branch is using its own range in the array, I don't have to
        // worry about access conflicts during the merging
        int[] temp = new int[list.length];
        mergeSort(list, 0, list.length / 2, list.length - 1, temp);
    }

    public static void mergeSort(int[] list, int start, int mid, int end, int[] temp) {
        if (end - start > 0) {
            // sort both halves
            mergeSort(list, start, start + (mid - start) / 2, mid, temp);
            mergeSort(list, mid + 1, mid + (end - mid + 1) / 2, end, temp);
            // merge them together
            merge(list, start, mid, end, temp);
        }
    }

    public static void merge(int[] list, int start, int mid, int end, int[] temp) {
        // start of the new merged location in the array
        int index = start;
        int a = start;
        int aEnd = mid;
        int b = mid + 1;
        int bEnd = end;

        // do while both the left and right indicies are in their respective
        // ranges
        while (a <= aEnd && b <= bEnd) {
            if (list[a] < list[b]) {
                temp[index++] = list[a++];
            } else {
                temp[index++] = list[b++];
            }
        }

        // if there's some left in a, add them to temp
        while (a <= aEnd) {
            temp[index++] = list[a++];
        }
        // if there's some left in b, add them to temp
        while (b <= bEnd) {
            temp[index++] = list[b++];
        }

        // copy the temp range back into the real list array
        /*
         for (int i = start; i <= end; i++) {
         list[i] = temp[i];
         }
         */
        
        // gonna use the built in array copy for speed
        System.arraycopy(temp, start, list, start, end - start + 1);
    }

    public static void swap(int[] list, int a, int b) {
        int temp = list[a];
        list[a] = list[b];
        list[b] = temp;
    }

}
