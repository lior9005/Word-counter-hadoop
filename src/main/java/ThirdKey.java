public class ThirdKey extends Key {

    public ThirdKey() {
        super();
    }

    public ThirdKey(String first, String second, String third) {
        super(first, second, third);
    }

    public ThirdKey(ThirdKey other) {
        super(other);
    }

    @Override
	public int compareTo(Key other) {
		int comp =0;
		comp = this.first.toString().compareTo(other.getFirst().toString());
		if(comp == 0)
			comp = this.second.toString().compareTo(other.getSecond().toString());
		if(comp == 0)
			comp = this.third.toString().compareTo(other.getThird().toString());									
		return comp;
	}
}