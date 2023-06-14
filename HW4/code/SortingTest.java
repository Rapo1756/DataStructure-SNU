import java.io.*;
import java.util.*;

public class SortingTest
{
	public static void main(String args[])
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		try
		{
			boolean isRandom = false;	// 입력받은 배열이 난수인가 아닌가?
			int[] value;	// 입력 받을 숫자들의 배열
			String nums = br.readLine();	// 첫 줄을 입력 받음
			if (nums.charAt(0) == 'r')
			{
				// 난수일 경우
				isRandom = true;	// 난수임을 표시

				String[] nums_arg = nums.split(" ");

				int numsize = Integer.parseInt(nums_arg[1]);	// 총 갯수
				int rminimum = Integer.parseInt(nums_arg[2]);	// 최소값
				int rmaximum = Integer.parseInt(nums_arg[3]);	// 최대값

				Random rand = new Random();	// 난수 인스턴스를 생성한다.

				value = new int[numsize];	// 배열을 생성한다.
				for (int i = 0; i < value.length; i++)	// 각각의 배열에 난수를 생성하여 대입
					value[i] = rand.nextInt(rmaximum - rminimum + 1) + rminimum;
			}
			else
			{
				// 난수가 아닐 경우
				int numsize = Integer.parseInt(nums);

				value = new int[numsize];	// 배열을 생성한다.
				for (int i = 0; i < value.length; i++)	// 한줄씩 입력받아 배열원소로 대입
					value[i] = Integer.parseInt(br.readLine());
			}

			// 숫자 입력을 다 받았으므로 정렬 방법을 받아 그에 맞는 정렬을 수행한다.
			while (true)
			{
				int[] newvalue = (int[])value.clone();	// 원래 값의 보호를 위해 복사본을 생성한다.
                char algo = ' ';

				if (args.length == 4) {
                    return;
                }

				String command = args.length > 0 ? args[0] : br.readLine();

				if (args.length > 0) {
                    args = new String[4];
                }

				long t = System.currentTimeMillis();
				switch (command.charAt(0))
				{
					case 'B':	// Bubble Sort
						newvalue = DoBubbleSort(newvalue);
						break;
					case 'I':	// Insertion Sort
						newvalue = DoInsertionSort(newvalue);
						break;
					case 'H':	// Heap Sort
						newvalue = DoHeapSort(newvalue);
						break;
					case 'M':	// Merge Sort
						newvalue = DoMergeSort(newvalue);
						break;
					case 'Q':	// Quick Sort
						newvalue = DoQuickSort(newvalue);
						break;
					case 'R':	// Radix Sort
						newvalue = DoRadixSort(newvalue);
						break;
					case 'S':	// Search
						algo = DoSearch(newvalue);
						break;
					case 'X':
						return;	// 프로그램을 종료한다.
					default:
						throw new IOException("잘못된 정렬 방법을 입력했습니다.");
				}
				if (isRandom)
				{
					// 난수일 경우 수행시간을 출력한다.
					System.out.println((System.currentTimeMillis() - t) + " ms");
				}
				else
				{
					// 난수가 아닐 경우 정렬된 결과값을 출력한다.
                    if (command.charAt(0) != 'S') {
                        for (int i = 0; i < newvalue.length; i++) {
                            System.out.println(newvalue[i]);
                        }
                    } else {
                        System.out.println(algo);
                    }
				}

			}
		}
		catch (IOException e)
		{
			System.out.println("입력이 잘못되었습니다. 오류 : " + e.toString());
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoBubbleSort(int[] value)
	{
		// TODO : Bubble Sort 를 구현하라.
		// value는 정렬안된 숫자들의 배열이며 value.length 는 배열의 크기가 된다.
		// 결과로 정렬된 배열은 리턴해 주어야 하며, 두가지 방법이 있으므로 잘 생각해서 사용할것.
		// 주어진 value 배열에서 안의 값만을 바꾸고 value를 다시 리턴하거나
		// 같은 크기의 새로운 배열을 만들어 그 배열을 리턴할 수도 있다.
		int n = value.length;
		for (int i = 0; i < n - 1; i++) {
			for (int j = 0; j < n - i - 1; j++) {
				if (value[j] > value[j + 1]) {
					int temp = value[j];
					value[j] = value[j + 1];
					value[j + 1] = temp;
				}
			}
		}
		return value;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoInsertionSort(int[] value)
	{
		// TODO : Insertion Sort 를 구현하라.
		int n = value.length;
		for (int i = 0; i < n; i++) {
			int insertValue = value[i];
			int j = i - 1;
			while (j >= 0 && value[j] > insertValue) value[j+1] = value[j--];
			value[j+1] = insertValue;
		}
		return value;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoHeapSort(int[] value)
	{
		// TODO : Heap Sort 를 구현하라.
		int n = value.length;
		for (int i = (n - 2) / 2; i >= 0; i--) percolateDown(value, i, value.length);
		for (int i = n - 1; i > 0; i--) {
			int temp = value[0];
			value[0] = value[i];
			value[i] = temp;
			percolateDown(value, 0, --n);
		}
		return value;
	}

	private static void percolateDown(int[] value, int k, int n)
	{
		int child = 2*k+1;
		int right = 2*k+2;
		if (child < value.length) {
			if (right < n && value[child] < value[right]) child = right;
			if (child < n && value[k] < value[child]) {
				int temp = value[k];
				value[k] = value[child];
				value[child] = temp;
				percolateDown(value, child, n);
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoMergeSort(int[] value)
	{
		// TODO : Merge Sort 를 구현하라.
		msort(value,0, value.length-1);
		return value;
	}

	private static void msort(int[] value, int l, int r)
	{
		if (l < r){
			int m = (l + r) / 2;
			msort(value, l, m);
			msort(value,m + 1, r);
			merge(value, l, m, r);
		}
	}

	private static void merge(int[] value, int l, int m, int r)
	{
		int leftSize = m - l + 1, rightSize = r - m;
		int[] leftArray = new int[leftSize];
		int[] rightArray = new int[rightSize];
		System.arraycopy(value, l, leftArray, 0, leftSize);
		System.arraycopy(value, m+1, rightArray, 0, rightSize);
		int i = 0, j = 0, k = l;
		while(i < leftSize && j < rightSize) {
			if(leftArray[i] < rightArray[j]) value[k++] = leftArray[i++];
			else value[k++] = rightArray[j++];
		}
		while(i < leftSize) value[k++] = leftArray[i++];
		while(j < rightSize) value[k++] = rightArray[j++];
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoQuickSort(int[] value)
	{
		// TODO : Quick Sort 를 구현하라.
		qsort(value, 0, value.length-1);
		return value;
	}

	private static void qsort(int[] value, int l, int r)
	{
		if (l < r) {
			int p = partition(value, l, r);
			qsort(value, l, p - 1);
			qsort(value, p + 1, r);
		}
	}
	private static int partition(int[] value, int l, int r)
	{
		int pivot = value[r];
		int i = l - 1;
		for (int j = l; j < r; j++) {
			if (value[j] < pivot) {
				int temp = value[++i];
				value[i] = value[j];
				value[j] = temp;
			}
		}
		int temp = value[i + 1];
		value[i + 1] = value[r];
		value[r] = temp;
		return i + 1;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoRadixSort(int[] value)
	{
		// TODO : Radix Sort 를 구현하라.
		int max = max(value);
		for (int exp = 1; max / exp > 0; exp *= 10) {
			countingSort(value, exp);
		}
		return value;
	}

	private static int max(int[] value) {
		int max = 0;
		for (int num : value) {
			max = Math.max(max, Math.abs(num));
		}
		return max;
	}
	// Assisted by ChatGPT from here
	private static void countingSort(int[] value, int exp)
	{
		int n = value.length;
		int[] output = new int[value.length];
		int[] count = new int[19];
		for (int j : value) {
			count[((j / exp) % 10) + 9]++;
		}
		for (int i = 1; i < 19; i++) {
			count[i] += count[i-1];
		}
		for (int i = n - 1; i >= 0; i--) {
			int digit = (value[i] / exp) % 10 + 9;
			output[count[digit] - 1] = value[i];
			count[digit]--;
		}
		System.arraycopy(output, 0, value, 0, n);

	}
	// Assisted by ChatGPT

	////////////////////////////////////////////////////////////////////////////////////////////////////
    private static char DoSearch(int[] value) {
		// TODO : Search 를 구현하라.
		if (Math.log10(max(value)) < 7) return 'R';
		if (value.length <= 50) return 'I';
		int countSorted = 0;
		for (int i = 0; i < value.length - 1; i++) {
			if (value[i] < value[i + 1]) countSorted++;
		}
		if (countSorted == value.length - 1) return 'I';
		if (countSorted > 0.75 * value.length || countSorted < 0.2 * value.length) return 'M';
		int[] table = new int[50021];
		Arrays.fill(table, 0);
		for (int i : value) {
			table[Math.floorMod(i, 50021)]++;
		}
		int maxCollision = 0;
		for (int i : table) {
			if(i > maxCollision) maxCollision = i;
		}
		double collisionRate = (double) (maxCollision - 1) / (value.length - 1);
		if (collisionRate == 1.00) return 'I';
		if (collisionRate >= 0.45) return 'H';
		if (collisionRate >= 0.02) return 'M';
		return 'Q'; // 여러 조건을 설정하고, 각 조건에 따라 B, I, H, M, Q, R 중 하나를 리턴하라.
	}
}
