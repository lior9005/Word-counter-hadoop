import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.Text;


public class ThirdKey implements WritableComparable<ThirdKey>{
	
    private Text first;
	private Text second;
	private Text third;

	public ThirdKey() {
		this.first = new Text();
		this.second = new Text();
		this.third = new Text();		
	}

    public ThirdKey(String string, String string2, String string3) {
        this.first = new Text(string);
		this.second = new Text(string2);
		this.third = new Text(string3);	
    }

    public ThirdKey(ThirdKey other) {
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
	public int compareTo(ThirdKey other) {
		int comp =0;
		comp = this.first.toString().compareTo(other.getFirst().toString());
		if(comp == 0)
			comp = this.second.toString().compareTo(other.getSecond().toString());
		if(comp == 0)
			comp = this.third.toString().compareTo(other.getThird().toString());									
		return comp;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getFirst(), getSecond(), getThird());
	}

    public String toString() {
		return first.toString() + " " + second.toString() + " " + third.toString();
	}

}

