import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.Text;

public class FourthKey extends Key {
	
    private Text key;

	public FourthKey() {
		this.key = new Text();	
	}

    public FourthKey(String key) {
        this.key = new Text(key);
    }

    public FourthKey(FourthKey other) {
		this.key = other.getKey();
	}

	public Text getKey() {
		return this.key;
	}

    @Override
	public void readFields(DataInput in) throws IOException {
		((Writable) key).readFields(in) ;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		((Writable) key).write(out) ;
	}

    @Override
	public int compareTo(Key other) {
		String[] key1 = ((Text) key).toString().split("\t");
        String[] key2 = ((Text) ((FourthKey) other).getKey()).toString().split("\t");
		String[] words1 = key1[0].split(" ");
		String[] words2 = key2[0].split(" ");
        int num = words1[0].compareTo(words2[0]);
        if(num == 0)
            num = words1[1].compareTo(words2[1]);
        if(num == 0)
            if(Double.parseDouble(key1[1]) > Double.parseDouble(key2[1]))
                num = -1;
            else
                num = 1;
        return num;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getKey());
	}

    public String toString() {
		return this.key.toString();
	}

}