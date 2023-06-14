import java.io.*;

public class Matching {
	static MyHashtable<MyString, MyAVLTree<MySortedList<MyString, Position>>> stringTable;
	static String currFilePath = "";
	static int lastLineNum = 0;

	public static void main(String args[]) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		stringTable = new MyHashtable<>(100);
		while (true) {
			try {
				String input = br.readLine();
				if (input.compareTo("QUIT") == 0)
					break;

				command(input);
			} catch (IOException e) {
				System.out.println("입력이 잘못되었습니다. 오류 : " + e.toString());
			}
		}
	}

	private static void command(String input) {
		char op = input.charAt(0);
		if (input.charAt(1) != ' ') {
			System.out.println("입력이 잘못되었습니다. 오류 : MissingSpaceException");
			return;
		}
		String arg = input.substring(2);
		switch (op) {
			case '<':
				inputData(arg);
				break;
			case '@':
				try {
					int idx = Integer.parseInt(arg);
					printData(idx);
				} catch (NumberFormatException | IndexOutOfBoundsException e) {
					System.out.println("입력이 잘못되었습니다. 오류 : " + e);
				}
				break;
			case '?':
				MySortedList<MyString, Position> positions = searchPattern(arg);
				if (positions.isEmpty())
					System.out.println(Position.ORIGIN);
				else
					positions.printList();
				break;
			case '/':
				int length = deleteString(new MyString(arg));
				System.out.println(length);
				break;
			case '+':
				int lineNum = addString(arg);
				System.out.println(lineNum);
				break;
			default:
				System.out.println("입력이 잘못되었습니다. 오류 : InvalidCommandException");
		}
	}

	private static void inputData(String filePath) {
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			currFilePath = filePath;
			lastLineNum = 0;
			stringTable.clear();
			String line;
			while ((line = br.readLine()) != null)
				addLine(line);
		} catch (IOException e) {
			System.out.println("입력이 잘못되었습니다. 오류 : " + e);
		}
	}

	private static void printData(int idx) {
		MyAVLTree<MySortedList<MyString, Position>> tree = stringTable.get(idx);
		if (tree == null) {
			System.out.println("EMPTY");
		} else {
			System.out.println(tree);
		}

	}

	private static MyAVLNode<MySortedList<MyString, Position>> findNode(MyString keyString) {
		MyAVLTree<MySortedList<MyString, Position>> tree = stringTable.search(keyString);
		return tree != null ? tree.search(new MySortedList<>(keyString)) : null;
	}

	private static MySortedList<MyString, Position> searchPattern(String pattern) {
		MySortedList<MyString, Position> resultList = new MySortedList<>(new MyString(pattern));
		for (int i = 0; i <= pattern.length() - 6; i++) {
			MyString keyString = new MyString(pattern.substring(i, i + 6));
			MyAVLNode<MySortedList<MyString, Position>> node = findNode(keyString);
			if (node == null) {
				resultList.clear();
				continue;
			}
			MySortedList<MyString, Position> positionList = node.item;
			if (i == 0) {
				resultList.addAll(positionList);
			} else {
				for (int j = 0; j < resultList.size(); j++) {
					Position pos = resultList.get(j);
					Position newPos = new Position(pos.line, pos.idx + i);
					if (!positionList.contains(newPos)) {
						resultList.remove(j);
						j--;
					}
				}
			}
			if (resultList.isEmpty())
				break;
		}
		return resultList;
	}

	private static int deleteString(MyString keyString) {
		MyAVLNode<MySortedList<MyString, Position>> node = findNode(keyString);
		if (node == null || !deleteStrFromFile(node.item))
			return 0;
		int retVal = node.item.size();
		inputData(currFilePath);
		return retVal;
	}

	private static boolean deleteStrFromFile(MySortedList<MyString, Position> positionList) {
		try (BufferedReader br = new BufferedReader(new FileReader(currFilePath))) {
			StringBuilder sb = new StringBuilder();
			String line;
			int lineNum = 0;
			int i = 0;
			int lastIdx = 0;
			while ((line = br.readLine()) != null) {
				lineNum++;
				if (line.length() < 6)
					continue;
				for (; i < positionList.size() && lineNum == positionList.get(i).line; i++) {
					int endIdx = positionList.get(i).idx - 1;
					if (lastIdx < endIdx)
						sb.append(line, lastIdx, endIdx);
					lastIdx = endIdx + 6;
				}
				sb.append(line.substring(lastIdx)).append("\n");
				lastIdx = 0;
			}
			String fileString = sb.toString();
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(currFilePath))) {
				bw.write(fileString);
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	private static int addString(String line) {
		addLine(line);
		return lastLineNum;
	}

	private static void addLine(String line) {
		lastLineNum++;
		for (int i = 0; i <= line.length() - 6; i++) {
			MyString keyString = new MyString(line.substring(i, i + 6));
			MyAVLTree<MySortedList<MyString, Position>> tree = stringTable.search(keyString);
			if (tree == null) {
				tree = new MyAVLTree<>();
				stringTable.insert(keyString, tree);
			}
			MySortedList<MyString, Position> positionList = new MySortedList<>(keyString);
			MyAVLNode<MySortedList<MyString, Position>> node = tree.search(positionList);
			if (node == null) {
				tree.insert(positionList);
			} else {
				positionList = node.item;
			}
			positionList.add(new Position(lastLineNum, i + 1));
		}
	}

}

class Position implements Comparable<Position> {
	public int line, idx;

	public Position(int l, int i) {
		this.line = l;
		this.idx = i;
	}

	public static final Position ORIGIN = new Position(0, 0);

	@Override
	public int compareTo(Position o) {
		int lineComp = Integer.compare(line, o.line);
		return lineComp != 0 ? lineComp : Integer.compare(idx, o.idx);
	}

	@Override
	public String toString() {
		return "(" + line + ", " + idx + ")";
	}
}

class MyListNode<T> {
	public T item;
	public MyListNode<T> next;

	public MyListNode() {
		item = null;
		next = null;
	}

	public MyListNode(T data) {
		item = data;
		next = null;
	}

	public MyListNode(T data, MyListNode<T> nextNode) {
		item = data;
		next = nextNode;
	}
}

class MySortedList<K extends Comparable<K>, E extends Comparable<E>> implements Comparable<MySortedList<K, E>> {
	private MyListNode<E> head;
	public K key;
	private int numItems;

	public MySortedList() {
		head = new MyListNode<>();
		numItems = 0;
		key = null;
	}

	public MySortedList(K k) {
		head = new MyListNode<>();
		numItems = 0;
		key = k;
	}

	public int size() {
		return numItems;
	}

	public boolean isEmpty() {
		return numItems == 0;
	}

	public void add(E e) {
		MyListNode<E> currNode = head.next;
		MyListNode<E> prevNode = head;
		while (currNode != null && currNode.item.compareTo(e) < 0) {
			prevNode = currNode;
			currNode = currNode.next;
		}
		prevNode.next = new MyListNode<>(e, currNode);
		numItems++;
	}

	public void addAll(MySortedList<K, E> positionList) {
		MyListNode<E> currNode = positionList.head.next;
		while (currNode != null) {
			add(currNode.item);
			currNode = currNode.next;
		}
	}

	public boolean remove(Object o) {
		MyListNode<E> currNode = head.next;
		MyListNode<E> prevNode = head;
		while (currNode != null) {
			if (currNode.item.equals(o)) {
				prevNode.next = currNode.next;
				numItems--;
				return true;
			}
			prevNode = currNode;
			currNode = currNode.next;
		}
		return false;
	}

	public void clear() {
		head = new MyListNode<>();
		numItems = 0;
	}

	public E get(int index) {
		if (index < 0 || index >= numItems)
			throw new IndexOutOfBoundsException();
		MyListNode<E> currNode = head.next;
		for (int i = 0; i < index; i++) {
			currNode = currNode.next;
		}
		return currNode.item;
	}

	public E remove(int index) {
		if (index < 0 || index >= numItems)
			throw new IndexOutOfBoundsException();
		MyListNode<E> currNode = head.next;
		MyListNode<E> prevNode = head;
		for (int i = 0; i < index; i++) {
			prevNode = currNode;
			currNode = currNode.next;
		}
		prevNode.next = currNode.next;
		numItems--;
		return currNode.item;
	}

	public boolean contains(E o) {
		MyListNode<E> currNode = head.next;
		while (currNode != null) {
			if (currNode.item.compareTo(o) == 0)
				return true;
			currNode = currNode.next;
		}
		return false;
	}

	@Override
	public String toString() {
		return key.toString();
	}

	public void printList() {
		StringBuilder sb = new StringBuilder();
		MyListNode<E> currNode = head.next;
		while (currNode != null) {
			sb.append(currNode.item);
			if (currNode.next != null)
				sb.append(" ");
			currNode = currNode.next;
		}
		System.out.println(sb.toString());
	}

	@Override
	public int compareTo(MySortedList<K, E> o) {
		return key.compareTo(o.key);
	}
}

class MyAVLNode<K extends Comparable<K>> {
	public K item;
	int height;
	MyAVLNode<K> left, right;

	public MyAVLNode(K x) {
		item = x;
		left = null;
		right = null;
		height = 0;
	}

	public MyAVLNode(K x,
			MyAVLNode<K> leftChild,
			MyAVLNode<K> rightChild,
			int h) {
		item = x;
		left = leftChild;
		right = rightChild;
		height = h;
	}
}

class MyAVLTree<K extends Comparable<K>> {
	public MyAVLNode<K> root;

	public MyAVLTree() {
		root = null;
	}

	public MyAVLNode<K> search(K x) {
		return searchItem(root, x);
	}

	private MyAVLNode<K> searchItem(MyAVLNode<K> tNode, K x) {
		if (tNode == null)
			return null;
		else if (tNode.item.compareTo(x) == 0)
			return tNode;
		else if (tNode.item.compareTo(x) > 0)
			return searchItem(tNode.left, x);
		else
			return searchItem(tNode.right, x);
	}

	private int getHeight(MyAVLNode<K> tNode) {
		if (tNode.right == null) {
			if (tNode.left == null)
				return 0;
			return 1 + tNode.left.height;
		} else if (tNode.left == null) {
			return 1 + tNode.right.height;
		} else
			return 1 + Math.max(tNode.right.height, tNode.left.height);
	}

	public void insert(K x) {
		root = insertItem(root, x);
	}

	private MyAVLNode<K> insertItem(MyAVLNode<K> tNode, K x) {
		if (tNode == null) {
			tNode = new MyAVLNode<>(x);
		} else if (x.compareTo(tNode.item) < 0) {
			tNode.left = insertItem(tNode.left, x);
			tNode.height = getHeight(tNode);
			tNode = balanceAVL(tNode);
		} else {
			tNode.right = insertItem(tNode.right, x);
			tNode.height = getHeight(tNode);
			tNode = balanceAVL(tNode);
		}
		return tNode;
	}

	public void delete(K x) {
		root = findAndDelete(root, x);
	}

	private MyAVLNode<K> findAndDelete(MyAVLNode<K> tNode, K x) {
		if (tNode == null) {
			return null;
		} else if (x.compareTo(tNode.item) == 0) {
			tNode = deleteNode(tNode);
		} else if (x.compareTo(tNode.item) < 0) {
			tNode.left = findAndDelete(tNode.left, x);
			tNode.height = getHeight(tNode);
			tNode = balanceAVL(tNode);
		} else {
			tNode.right = findAndDelete(tNode.right, x);
			tNode.height = getHeight(tNode);
			tNode = balanceAVL(tNode);
		}
		return tNode;
	}

	private MyAVLNode<K> deleteNode(MyAVLNode<K> tNode) {
		if (tNode.left == null && tNode.right == null)
			return null;
		else if (tNode.left == null)
			return tNode.right;
		else if (tNode.right == null)
			return tNode.left;
		else {
			returnPair rPair = deleteMinItem(tNode.right);
			tNode.item = rPair.item;
			tNode.right = rPair.node;
			tNode.height = getHeight(tNode);
			tNode = balanceAVL(tNode);
			return tNode;
		}
	}

	private returnPair deleteMinItem(MyAVLNode<K> tNode) {
		if (tNode.left == null)
			return new returnPair(tNode.item, tNode.right);
		else {
			returnPair rPair = deleteMinItem(tNode.left);
			tNode.left = rPair.node;
			tNode.height = getHeight(tNode);
			tNode = balanceAVL(tNode);
			rPair.node = tNode;
			return rPair;
		}
	}

	private class returnPair {
		K item;
		MyAVLNode<K> node;

		private returnPair(K it, MyAVLNode<K> lnd) {
			item = it;
			node = lnd;
		}
	}

	private MyAVLNode<K> balanceAVL(MyAVLNode<K> tNode) {
		MyAVLNode<K> returnNode = tNode;
		int LHeight = tNode.left != null ? tNode.left.height : -1;
		int RHeight = tNode.right != null ? tNode.right.height : -1;
		if (LHeight + 2 <= RHeight) {
			assert tNode.right != null;
			int RLHeight = tNode.right.left != null ? tNode.right.left.height : -1;
			int RRHeight = tNode.right.right != null ? tNode.right.right.height : -1;
			if (RLHeight > RRHeight) {
				tNode.right = rightRotate(tNode.right);
			}
			returnNode = leftRotate(tNode);
		} else if (LHeight >= RHeight + 2) {
			assert tNode.left != null;
			int LLHeight = tNode.left.left != null ? tNode.left.left.height : -1;
			int LRHeight = tNode.left.right != null ? tNode.left.right.height : -1;
			if (LLHeight < LRHeight) {
				tNode.left = leftRotate(tNode.left);
			}
			returnNode = rightRotate(tNode);
		}
		return returnNode;
	}

	private MyAVLNode<K> leftRotate(MyAVLNode<K> t) {
		MyAVLNode<K> RChild = t.right;
		assert RChild != null;
		MyAVLNode<K> RLChild = RChild.left;
		RChild.left = t;
		t.right = RLChild;
		t.height = getHeight(t);
		RChild.height = getHeight(RChild);
		return RChild;
	}

	private MyAVLNode<K> rightRotate(MyAVLNode<K> t) {
		MyAVLNode<K> LChild = t.left;
		assert LChild != null;
		MyAVLNode<K> LRChild = LChild.right;
		LChild.right = t;
		t.left = LRChild;
		t.height = getHeight(t);
		LChild.height = getHeight(LChild);
		return LChild;
	}

	@Override
	public String toString() {
		String s = preorderTraversal(root);
		return s.substring(0, s.length() - 1);
	}

	private String preorderTraversal(MyAVLNode<K> node) {
		if (node == null)
			return "";
		else {
			return node.item + " " + preorderTraversal(node.left) + preorderTraversal(node.right);
		}
	}
}

class MyString implements Comparable<MyString> {
	String str;

	public MyString(String s) {
		str = s;
	}

	@Override
	public int compareTo(MyString s) {
		return str.compareTo(s.str);
	}

	@Override
	public String toString() {
		return str;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		for (int i = 0; i < str.length(); i++) {
			hash += str.charAt(i);
		}
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MyString) {
			MyString s = (MyString) obj;
			return str.equals(s.str);
		}
		return false;
	}
}

class MyHashtable<K, V> {
	private final V[] table;
	int capacity;
	int numItems;

	public MyHashtable(int n) {
		table = (V[]) new Object[n];
		capacity = n;
		numItems = 0;
	}

	private int hash(K x) {
		return x.hashCode() % capacity;
	}

	public V search(K key) {
		return table[hash(key)];
	}

	public V get(int idx) {
		return table[idx];
	}

	public boolean insert(K key, V item) {
		int slot = hash(key);
		if (table[slot] == null) {
			table[slot] = item;
			numItems++;
			return true;
		}
		return false;
	}

	public boolean delete(K key) {
		if (isEmpty())
			return false;
		int slot = hash(key);
		if (table[slot] == null)
			return false;
		table[slot] = null;
		numItems--;
		return true;
	}

	public boolean isEmpty() {
		return numItems == 0;
	}

	public void clear() {
		for (int i = 0; i < capacity; i++) {
			table[i] = null;
		}
		numItems = 0;
	}
}