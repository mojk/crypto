import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import javax.xml.bind.DatatypeConverter;

public class CBCXor {
	
	public static class Block { // creating a block object so we can store each 12bytes into an object when splitting the encrypted list
		//public final byte[] msg;
		public final ArrayList<Byte> msg;

		public Block(ArrayList<Byte> msg) { // constructor for the Block Object
			this.msg = msg;
	}
}

	public static void main(String[] args) {
		String filename = "input_cbc.txt";
		byte[] first_block = null;
		byte[] encrypted = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			first_block = br.readLine().getBytes();
			encrypted = DatatypeConverter.parseHexBinary(br.readLine());
			br.close();
		} catch (Exception err) {
			System.err.println("Error handling file.");
			err.printStackTrace();
			System.exit(1);
		}
		String m = recoverMessage(first_block, encrypted);
		System.out.println("Recovered message: " + m);
	}
	/**
	 * Recover the encrypted message (CBC encrypted with XOR, block size = 12).
	 * 
	 * @param first_block
	 *            We know that this is the value of the first block of plain
	 *            text.
	 * @param encrypted
	 *            The encrypted text, of the form IV | C0 | C1 | ... where each
	 *            block is 12 bytes long.
	 */
	private static String recoverMessage(byte[] first_block, byte[] encrypted) {
		
		// List containing all block objects
		ArrayList<Block> AllBlocks = new ArrayList<Block>();
		// encrypted converted into ArrayList
		// first_block converted into ArrayList
		ArrayList<Byte> encrypted_converted = new ArrayList<Byte>();
		ArrayList<Byte> first_block_converted = new ArrayList<Byte>();
		// IV vector
		ArrayList<Byte> recovered_msg = new ArrayList<Byte>();

		for(int j = 0; j < first_block.length; j++) {
			first_block_converted.add(first_block[j]);
		}
		for(int i=0; i < encrypted.length; i++) {
			encrypted_converted.add(encrypted[i]);
		}
		// set the first block into the encrypted block
		// initialize empty Block objects and adding them to the List in order c1-c6
		Block iv = new Block(new ArrayList<Byte>());
		Block c0 = new Block(new ArrayList<Byte>()); AllBlocks.add(c0); Block c1 = new Block(new ArrayList<Byte>()); AllBlocks.add(c1); 
		Block c2 = new Block(new ArrayList<Byte>()); AllBlocks.add(c2); Block c3 = new Block(new ArrayList<Byte>()); AllBlocks.add(c3);
		Block c4 = new Block(new ArrayList<Byte>()); AllBlocks.add(c4); Block c5 = new Block(new ArrayList<Byte>()); AllBlocks.add(c5);
		
		System.out.println("Length of the encrypted msg is: " + encrypted_converted.size() +" bytes");
		System.out.println("Which means we need " + encrypted_converted.size() / 12 + " blocks, where each block contains 12 bytes");
		System.out.println("AllBlocks Size = " + AllBlocks.size());

		int position = 0; // starting index
		// For every block in our dynamic list, we want to copy 12 bytes into each block
		for(Block b : AllBlocks) {
			for(int i = position; i < encrypted_converted.size(); i++) {
				b.msg.add(encrypted_converted.get(i));
				if(i % 11 == 0) {
					position =+ 12;
				}
			}
			// Now we want to get the IV, c0 XOR p0 = IV
			for(int ind = 0; ind < 11; ind++) {
				byte byt = (byte) (c0.msg.get(ind) ^ first_block_converted.get(ind));
				iv.msg.add(ind,byt);
			}
			// Starting to decrypt the next blocks in line, c1...c5
			for(int j = 1; j < AllBlocks.size(); j++) {
				for(int i = 1; i < 12; i++) {
					recovered_msg.add( (byte) (iv.msg.get(i) ^ AllBlocks.get(j-1).msg.get(i) ^ AllBlocks.get(j).msg.get(i) ) );
			}
		}
	}
		System.out.println(recovered_msg.size());
		return "Fin";
	}
}