import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Genre, Title 을 관리하는 영화 데이터베이스.
 * 
 * MyLinkedList 를 사용해 각각 Genre와 Title에 따라 내부적으로 정렬된 상태를  
 * 유지하는 데이터베이스이다. 
 */
public class MovieDB {
	//dummy head
	MyLinkedList<MovieList> movieDB;
    public MovieDB() {
		movieDB = new MyLinkedList<>();
    }

    public void insert(MovieDBItem item) {
		String genre = item.getGenre();
		String title = item.getTitle();
		Node<MovieList> last = movieDB.head;
		while(last.getNext() != null && last.getNext().getItem().getGenre().compareTo(genre) < 0)
			last = last.getNext();
		Node<MovieList> nextNode = last.getNext();
		if(nextNode != null && nextNode.getItem().getGenre().equals(genre)) {
			if(!nextNode.getItem().contains(title)) {
				nextNode.getItem().add(title);
			}
		}
		else {
			MovieList mList = new MovieList();
			mList.head.setItem(genre);
			mList.add(title);
			last.insertNext(mList);
			movieDB.numItems++;
		}
    }

    public void delete(MovieDBItem item) {
		String genre = item.getGenre();
		String title = item.getTitle();
		Node<MovieList> last = movieDB.head;
		while(last.getNext() != null) {
			last = last.getNext();
			if(last.getItem().getGenre().equals(genre)) {
				if(last.getItem().contains(title)) {
					last.getItem().remove(title);
					break;
				}
			}
		}
    }

    public MyLinkedList<MovieDBItem> search(String term) {
		MyLinkedList<MovieDBItem> results = new MyLinkedList<MovieDBItem>();
    	for(MovieList mList : movieDB){
			String genre = mList.head.getItem();
			for(String title : mList){
				if(title.contains(term)){
					results.add(new MovieDBItem(genre, title));
				}
			}
		}
        return results;
    }
    
    public MyLinkedList<MovieDBItem> items() {
		MyLinkedList<MovieDBItem> results = new MyLinkedList<MovieDBItem>();
		for(MovieList mList : movieDB){
			String genre = mList.getGenre();
			for(String title : mList){
				results.add(new MovieDBItem(genre, title));
			}
		}
        
    	return results;
    }
}

class Genre extends Node<String> implements Comparable<Genre> {
	public Genre(String name) {
		super(name);
	}
	
	@Override
	public int compareTo(Genre o) {
		return getItem().compareTo(o.getItem());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 7;
		result = prime * result + ((getItem() == null) ? 0 : getItem().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass()) {
			return false;
		}
		Genre other = (Genre) obj;
		return getItem().equals(other.getItem());
	}
}

class MovieList implements ListInterface<String> {

	Genre head;
	int numItems;
	public MovieList() {
		head = new Genre(null);
	}

	public String getGenre() {
		return head.getItem();
	}

	@Override
	public Iterator<String> iterator() {
		return new MovieIterator(this);
	}

	@Override
	public boolean isEmpty() {
		return head.getNext() == null;
	}

	@Override
	public int size() {
		return numItems;
	}

	@Override
	public void add(String item) {
		Node<String> last = head;
		while(last.getNext() != null && item.compareTo(last.getNext().getItem()) > 0){
			last = last.getNext();
		}
		last.insertNext(item);
		numItems++;
	}

	public boolean contains(String item) {
		Node<String> last = head;
		while(last.getNext() != null && !last.getNext().getItem().equals(item)){
			last = last.getNext();
		}
		return last.getNext() != null;
	}

	@Override
	public String first() {
		return head.getNext().getItem();
	}

	@Override
	public void removeAll() {
		head.setNext(null);
		numItems = 0;
	}

	public void remove(String item) {
		Node<String> last = head;
		while(last.getNext() != null){
			if(last.getNext().getItem().equals(item)){
				last.removeNext();
				break;
			}
			last = last.getNext();
		}
	}
}

class MovieIterator implements Iterator<String>{

	private MovieList list;
	private Node<String> curr;
	private Node<String> prev;

	public MovieIterator(MovieList list){
		this.list = list;
		this.curr = list.head;
		this.prev = null;
	}

	@Override
	public boolean hasNext() {
		return curr.getNext() != null;
	}

	@Override
	public String next() {
		if(!hasNext())
			throw new NoSuchElementException();
		prev = curr;
		curr = curr.getNext();
		return curr.getItem();
	}

	@Override
	public void remove() {
		if (prev == null)
			throw new IllegalStateException("next() should be called first");
		if (curr == null)
			throw new NoSuchElementException();
		prev.removeNext();
		list.numItems -= 1;
		curr = prev;
		prev = null;
	}
}
