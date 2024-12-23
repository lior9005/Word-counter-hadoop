import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.Text;

public class FirstKey implements WritableComparable<FirstKey>{
	
    private Text first;
	private Text second;
	private Text third;

	public FirstKey() {
		this.first = new Text();
		this.second = new Text();
		this.third = new Text();		
	}

    public FirstKey(String string, String string2, String string3) {
        this.first = new Text(string);
		this.second = new Text(string2);
		this.third = new Text(string3);	
    }

    public FirstKey(FirstKey other) {
		this.first = other.getFirst();
		this.second = other.getSecond();
		this.third = other.getThird();		
	}

	public Text getFirst() {
		return this.first;
	}

	public Text getSecond() {
		return this.second;
	}

	public Text getThird() {
		return this.third;
	}

    @Override
	public void readFields(DataInput in) throws IOException {
		((Writable) first).readFields(in) ;
		((Writable) second).readFields(in) ;
		((Writable) third).readFields(in) ;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		((Writable) first).write(out) ;
		((Writable) second).write(out) ;
		((Writable) third).write(out) ;
	}

    @Override
	public int compareTo(FirstKey other) {
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

	@Override
	public int hashCode() {
		return Objects.hash(getFirst(), getSecond(), getThird());
	}

    public String toString() {
		return first.toString() + " " + second.toString() + " " + third.toString();
	}

}