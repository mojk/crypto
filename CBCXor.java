import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import javax.xml.bind.DatatypeConverter;

public class CBCXor {
	
	public static class Block { // creating a block object so we can store each 12bytes into an object when splitting the encrypted list
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

		ArrayList<Block> AllBlocks = new ArrayList<Block>(); // contains all the blocks in the encrypted msg
		ArrayList<Byte> encrypted_converted = new ArrayList<Byte>(); // 
		ArrayList<Byte> first_block_converted = new ArrayList<Byte>(); // Convert byte[] to ArrayList<Byte>
		ArrayList<Byte> recovered_msg = new ArrayList<Byte>(); // 

		System.out.print("Plaintext of P1 as bytes ");
		for(int j = 0; j < first_block.length; j++) {
			first_block_converted.add( (byte) first_block[j]);
			System.out.print(first_block_converted.get(j));
		}
		System.out.println("");

		System.out.print("Encrypted message as bytes ");
		for(int i= 0; i < encrypted.length; i++) {
			encrypted_converted.add( (byte) encrypted[i]);
			System.out.print(encrypted_converted.get(i));
		}
		System.out.println("");

		Block iv = new Block(new ArrayList<Byte>());
		Block c0 = new Block(new ArrayList<Byte>()); AllBlocks.add(c0); Block c1 = new Block(new ArrayList<Byte>()); AllBlocks.add(c1); 
		Block c2 = new Block(new ArrayList<Byte>()); AllBlocks.add(c2); Block c3 = new Block(new ArrayList<Byte>()); AllBlocks.add(c3);
		Block c4 = new Block(new ArrayList<Byte>()); AllBlocks.add(c4); Block c5 = new Block(new ArrayList<Byte>()); AllBlocks.add(c5);
		
		System.out.println("Length of the encrypted msg is: " + encrypted_converted.size() +" bytes");
		System.out.println("Which means we need " + encrypted_converted.size() / 12 + " blocks, where each block contains 12 bytes");
		System.out.println("AllBlocks Size = " + AllBlocks.size());

		//TODO
		int pos = 0;
			for(Block b : AllBlocks) {
				System.out.println("Starting to add at position.." + pos);
				for(int i = pos; i < encrypted_converted.size(); i++) {
					b.msg.add(encrypted_converted.get(i));
					pos++;
					if(pos % 12 == 0) {
						break;
					}
				}
			}
			//TODO VERIFY THAT THE BLOCK ACTUALLY HAS SOMETHING IN THEM
			int blocknr = 0;
			for(Block cpblocks: AllBlocks) {
				System.out.println("Printing contents of block: " + blocknr);
				blocknr++;
				for(int i = 0; i < cpblocks.msg.size(); i++) {
					System.out.print(cpblocks.msg.get(i));
				}
				System.out.println("");
			}

			// Now we want to get the IV, c0 XOR p0 = IV
			for(int ind = 0; ind < 11; ind++) {
				byte byt = (byte) (c0.msg.get(ind) ^ first_block_converted.get(ind));
				iv.msg.add(byt);
			}
			// Starting to decrypt the next blocks in line, c1...c5
			for(int j = 1; j < AllBlocks.size(); j++) {
				for(int i = 0; i < 11; i++) {
					// IV[0-11] XOR Cj-1[0-11] XOR Cj[0-11]
					recovered_msg.add((byte) (iv.msg.get(i) ^ AllBlocks.get(j-1).msg.get(i) ^ AllBlocks.get(j).msg.get(i) ));
			}
		}
		ArrayList<String> message = new ArrayList<String>();
		for(int i = 0; i < recovered_msg.size(); i++) {
			message.add(Byte.toString(recovered_msg.get(i)));
		}
		for(int i = 0; i < message.size(); i++) {
			System.out.print(message.get(i));
		}

		return "Fin";
	}
}
