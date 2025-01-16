import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.Text;


public abstract class Key implements WritableComparable<Key>{
	
    protected Text first;
	protected Text second;
	protected Text third;

	public Key() {
		this.first = new Text();
		this.second = new Text();
		this.third = new Text();		
	}

    public Key(String string, String string2, String string3) {
        this.first = new Text(string);
		this.second = new Text(string2);
		this.third = new Text(string3);	
    }

    public Key(Key other) {
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
	public abstract int compareTo(Key other);

	@Override
	public int hashCode() {
		return Objects.hash(getFirst(), getSecond(), getThird());
	}

    public String toString() {
		return first.toString() + " " + second.toString() + " " + third.toString();
	}
}