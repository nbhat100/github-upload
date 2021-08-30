/**
 * Nihal Bhat <nsb170002>
 * CS 3345.001
 * 
 * This program creates a generic hash table class that uses linear probing to resolve collisions. The hash table stores Entry
 * objects, which each Entry object has a key and a value. Entries are inserted based on the hashCode value of the key. 
 * The table is rehashed once it reaches a load factor of 0.5. The following functions are implemented: insert(key, value), find
 * a key and return the value if the key is in the hash table, delete an entry given a key, rehash function, a get hashValue function
 * given a key that returns the index before probing, a getLocation of an entry given a key that returns the index after probing,
 * and a toString function that displays the hashTable with its Entries, in the format of key, value. If the entry is deleted and the
 * table has not been rehashed after deleting it, then the entry is displayed as deleted.
 */
public class LinearProbingHashTable<K, V> {
	
	// private static inner class for Entry
	private static class Entry<K, V> {
		private K key;
		private V value;
		private boolean isDeleted;  // flag for if the entry is deleted or not. By default, it is false.
		
		public Entry(K ky, V val, boolean deleted) {
			key = ky;
			value = val;
			isDeleted = deleted;
		}

		public boolean isDeleted() {
			return isDeleted;
		}

		public void setDeleted(boolean isDeleted) {
			this.isDeleted = isDeleted;
		}

		public K getKey() {
			return key;
		}


		public V getValue() {
			return value;
		}

	}
	
	// LinearProbingHashTable instance variables.
	//An array of entries with given key and value, and the total size of the table.
	private Entry<K, V> table[];
	private int size;
	
	public LinearProbingHashTable() {
		table = null;
		size = 0;
	}
	
	public LinearProbingHashTable(Entry<K, V> hashTable[], int length) {
		table = hashTable;
		size = length;
	}

	public Entry<K, V>[] getTable() {
		return table;
	}

	public void setTable(Entry<K, V>[] table) {
		this.table = table;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
	/*
	 * Function to insert an Entry into the hash table, given the key and value
	 * If the key or value is null, then throw an IllegalArgumentException
	 * Count the number of elements that are not null and not deleted. If the count is greater than or equal to half the size,
	 * then rehash the hash table. 
	 * After rehashing if necessary, find the index to insert. Keep checking the next available index to see if the entry is null
	 * or deleted.
	 * Returns true if successfully inserted, false if adding a duplicate key.
	 */
	public boolean insert(K key, V value) {
		if (key == null || value == null)
			throw new IllegalArgumentException();
		else {
			int count = 0;
			// count the number of active Entry objects in the hash table (not null and not deleted)
			for (int k = 0; k < size; k++) {
				// skip null and deleted entries
				if (table[k] == null || table[k].isDeleted() == true)
					continue;
				else
					count++;
			}
			// Rehash and then attempt to insert the given key and value
			if (count >= size / 2) {
				rehash();
				int index = Math.abs(key.hashCode()) % size;
				// collision occurred
				if (table[index] != null) {
					// linear probing. Keep trying to find the next available index until the Entry object in the hash
					// table is null or deleted
					while (table[index] != null && table[index].isDeleted() == false) {
						// if duplicate key, return false
						if (table[index].getKey().equals(key)) {
							return false;
						}
						index++;
						index = index % size;
					}
				}
				// insert into the hash table and return true
				table[index] = new Entry<K, V>(key, value, false);
				return true;
			}
			// Rehashing not necessary, so attempt to add into the hash table
			int index = Math.abs(key.hashCode()) % size;
			// Found index immediately without probing
			if (table[index] == null || table[index].isDeleted() == true) {
				table[index] = new Entry<K, V>(key, value, false);
				return true;
			}
			// linear probing
			else {
				while (table[index] != null && table[index].isDeleted() == false) {
					if (table[index].getKey().equals(key))
						return false;
					index++;
					index = index % size;
				}
				// insert into the hash table and return true
				table[index] = new Entry<K, V>(key, value, false);
				return true;
			}
				
		}
	}
	
	/*
	 * Find the specified key and return its matching value.
	 * If the parameter is null, then throw exception. Else, loop through the hash table and try to find the key.
	 * If the current key matches the given key, return the Entry object's value.
	 */
	public V find(K key) {
		if (key == null)
			throw new IllegalArgumentException();
		else {
			for (int k = 0; k < size; k++) {
				// skip null Entries
				if (table[k] == null)
					continue;
				else {
					// if the current key matches the parameter key, and the Entry is not deleted, return the vaplue
					if ((table[k].getKey().equals(key) && table[k].isDeleted() == false))
						return table[k].getValue();
				}
			}
			return null;
		}
	}
	
	/*
	 * Finds the given key and mark its Entry as deleted.
	 */
	public boolean delete(K key) {
		if (key == null)
			throw new IllegalArgumentException();
		else {
			// go through each Entry in the hash table and see if it matches the given key.
			// If it matches, then mark it as deleted
			for (int k = 0; k < size; k++) {
				// skip null entries
				if (table[k] == null)
					continue;
				else if (table[k].getKey().equals(key) && table[k].isDeleted() == false) {
					table[k].setDeleted(true);
					return true;
				}
			}
			// return false if couldn't find the key or if it was already deleted
			return false;
		}
	}
	
	/*
	 * Rehash the hash table by doubling the size of the array and reinserting the Entries
	 */
	private void rehash() {
		// Create a new Entry array with double of the current size
		Entry<K, V> hashTbl[] = new Entry[size * 2];
		for (int k = 0; k < size; k++) {
			// Don't need to reinsert deleted Entries, so skip 
			if (table[k] == null || table[k].isDeleted() == true)
				continue;
			// find new index
			int index = table[k].getKey().hashCode() % (size * 2);
			if (hashTbl[index] == null)
				hashTbl[index] = table[k]; // insert into array
			else { // linear probing
				while (hashTbl[index] != null) {
					index++;
					index = index % (size * 2);
				}
				hashTbl[index] = table[k]; // insert into array
			}
		}
		// set the array created in this function as the instance variable hash table
		// set the size to double of the current value
		this.setTable(hashTbl);
		this.setSize(size * 2);
	}
	
	/*
	 * Return the hash value or the index to be inserted, before probing happens.
	 * This is to check if linear probing worked correctly
	 */
	public int getHashValue(K key) {
		if (key == null)
			throw new IllegalArgumentException();
		// function to find the index
		return Math.abs(key.hashCode()) % size;
	}
	
	/*
	 * Function to get the location of the given key from the hash table, after probing occurs, if any.
	 */
	public int getLocation(K key) {
		// throw exception if the parameter is null
		if (key == null)
			throw new IllegalArgumentException();
		// Find the given key, and return the current index
		for (int k = 0; k < size; k++) {
			if (table[k] != null) {
				// if the current key matched the given key and the Entry is not deleted, return the current index
				if ((table[k].getKey().equals(key)) && table[k].isDeleted() == false)
					return k;
			}
		}
		return -1; // Return -1 if the key is not there in the hash table
	}
	
	/*
	 * toString function to print the hash table. 
	 * Prints the index, and then a format of key, value.
	 * If the entry is deleted, then a tab is placed after the value and then marked as deleted.
	 */
	@Override
	public String toString() {
		String str = "";
		for (int k = 0; k < size; k++) {
			str += k; // print the index
			// if the Entry is not null, then print the key and value, and if it is deleted, then print deleted.
			if (table[k] != null) {
				if (k < 10)
					str += String.format("%9s", table[k].getKey()) + ", " + table[k].getValue();
				else
					str += String.format("%8s", table[k].getKey()) + ", " + table[k].getValue();
				if (table[k].isDeleted() == true)
					str += "\tdeleted\n";
				else
					str += "\n";
			}
			else
				str += "\n";
		}
		return str;
	}
	
	public static void main(String[] args) {
		int length = 13;
		Entry<Integer, String> table[];
		table = new Entry[length];
		LinearProbingHashTable<Integer, String> hashTable = new LinearProbingHashTable<Integer, String>(table, length);
		
		// Test functions of inserting and deleting
		hashTable.insert(338, "FQtVIKqO");
		hashTable.insert(338, "FQtVIKqO");  // duplicate value, not added again
		hashTable.insert(291, "axpgtwDf");
		hashTable.insert(368, "NbZSbBqy");
		hashTable.insert(174, "dltOtaZZ");
		hashTable.insert(16, "LksugHZv");
		hashTable.delete(174);
		hashTable.insert(86, "blZjXDMk");
		hashTable.insert(359, "AFEECDpt");
		hashTable.insert(461, "RIzzoJAm");
		hashTable.delete(16);
		hashTable.insert(344, "MEeZrREf");
		hashTable.insert(265, "sBfGIaKa");
		hashTable.insert(344, "MEeZrREf");   // duplicate value, not added again
		System.out.println(hashTable.toString());
		
		// Testing find function
		String value = hashTable.find(291);
		if (value != null)
			System.out.println("Value for key 291: " + value);
		else
			System.out.println("Value for key 291: The key is not in the hash table");
		
		String value2 = hashTable.find(359);
		if (value2 != null)
			System.out.println("Value for key 359: " + value2);
		else
			System.out.println("Value for key 359: The key is not in the hash table");
		
		String value3 = hashTable.find(10);
		if (value3 != null)
			System.out.println("Value for key 10: " + value3);
		else
			System.out.println("Value for key 10: The key is not in the hash table");
		
		System.out.println();
		
		// Testing getHashValue and getLocation functions. Go through the hash table and 
		// print each entry's key's hash value and location after probing
		for (int index = 0; index < hashTable.getSize(); index++) {
			Entry<Integer, String> tbl[] = hashTable.getTable();
			if (tbl[index] != null && tbl[index].isDeleted() == false) {
				int key = tbl[index].getKey();
				System.out.println("Hash value for " + key + ": " + hashTable.getHashValue(key));
				System.out.println("Index location for " + key + ": " + hashTable.getLocation(key));
				System.out.println();
			}
		}
	}
}
