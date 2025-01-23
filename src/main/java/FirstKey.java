public class FirstKey extends Key {

    public FirstKey() {
        super();
    }

    public FirstKey(String first, String second, String third) {
        super(first, second, third);
    }

    public FirstKey(FirstKey other) {
        super(other);
    }

    @Override
	public int compareTo(Key other) {
		int num =0;
		//1 word < 2 words < 3 words
		if(!third.toString().equals("*") && other.getThird().toString().equals("*"))
            return 1;
        if(third.toString().equals("*") && !other.getThird().toString().equals("*"))
            return -1;
        if(!second.toString().equals("*") && other.getSecond().toString().equals("*"))
			return 1;
        if(second.toString().equals("*") && !other.getSecond().toString().equals("*"))
			return -1;

		//if number of words are the same, order lexicographically
		if(num == 0)				
            num = first.toString().compareTo(other.getFirst().toString());
		if(num == 0)
            num = second.toString().compareTo(other.getSecond().toString());
		if(num == 0)
            num = third.toString().compareTo(other.getThird().toString());									
		return num;
	}
}