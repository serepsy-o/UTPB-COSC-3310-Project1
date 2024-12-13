/**
 * @auth
 */

import java.util.Arrays;

/**
 * <h1>UInt</h1>
 * Represents an unsigned integer using a boolean array to store the binary representation.
 * Each bit is stored as a boolean value, where true represents 1 and false represents 0.
 *
 * @author Tim Fielder
 * @version 1.0 (Sept 30, 2024)
 */
public class UInt {

    // The array representing the bits of the unsigned integer.
    protected boolean[] bits;

    // The number of bits used to represent the unsigned integer.
    protected int length;

    /**
     * Constructs a new UInt by cloning an existing UInt object.
     *
     * @param toClone The UInt object to clone.
     */
    public UInt(UInt toClone) {
        this.length = toClone.length;
        this.bits = Arrays.copyOf(toClone.bits, this.length);
    }

    /**
     * Constructs a new UInt from an integer value.
     * The integer is converted to its binary representation and stored in the bits array.
     *
     * @param i The integer value to convert to a UInt.
     */
    public UInt(int i) {
        // Determine the number of bits needed to store i in binary format.
        length = (int)(Math.ceil(Math.log(i)/Math.log(2.0)) + 1);
        bits = new boolean[length];

        // Convert the integer to binary and store each bit in the array.
        for (int b = length-1; b >= 0; b--) {
            // We use a ternary to decompose the integer into binary digits, starting with the 1s place.
            bits[b] = i % 2 == 1;
            // Right shift the integer to process the next bit.
            i = i >> 1;
        }

        // make sure boolean array always begins with a leading 0
        if (bits[0]) {
            this.padWithLeadingZeroes(1);
        }
    }

    /**
     * Creates and returns a copy of this UInt object.
     *
     * @return A new UInt object that is a clone of this instance.
     */
    @Override
    public UInt clone() {
        return new UInt(this);
    }

    /**
     * Creates and returns a copy of the given UInt object.
     *
     * @param u The UInt object to clone.
     * @return A new UInt object that is a copy of the given object.
     */
    public static UInt clone(UInt u) {
        return new UInt(u);
    }

    /**
     * Converts this UInt to its integer representation.
     *
     * @return The integer value corresponding to this UInt.
     */
    public int toInt() {
        int t = 0;
        // Traverse the bits array to reconstruct the integer value.
        for (int i = 0; i < length; i++) {
            // Again, using a ternary to now re-construct the int value, starting with the most-significant bit.
            t = t + (bits[i] ? 1 : 0);
            // Shift the value left for the next bit.
            t = t << 1;
        }
        return t >> 1; // Adjust for the last shift.
    }

    /**
     * Static method to retrieve the int value from a generic UInt object.
     *
     * @param u The UInt to convert.
     * @return The int value represented by u.
     */
    public static int toInt(UInt u) {
        return u.toInt();
    }

    /**
     * Returns a String representation of this binary object with a leading 0b.
     *
     * @return The constructed String.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("0b");
        // Construct the String starting with the most-significant bit.
        for (int i = 0; i < length; i++) {
            // Again, we use a ternary here to convert from true/false to 1/0
            s.append(bits[i] ? "1" : "0");
        }
        return s.toString();
    }

    /**
     * Performs a logical AND operation using this.bits and u.bits, with the result stored in this.bits.
     *
     * @param u The UInt to AND this against.
     */
    public void and(UInt u) {
        // We want to traverse the bits arrays to perform our AND operation.
        // But keep in mind that the arrays may not be the same length.
        // So first we use Math.min to determine which is shorter.
        // Then we need to align the two arrays at the 1s place, which we accomplish by indexing them at length-i-1.
        for (int i = 0; i < Math.min(this.length, u.length); i++) {
            this.bits[this.length - i - 1] =
                    this.bits[this.length - i - 1] &
                            u.bits[u.length - i - 1];
        }
        // In the specific case that this.length is greater, there are additional elements of
        //   this.bits that are not getting ANDed against anything.
        // Depending on the implementation, we may want to treat the operation as implicitly padding
        //   the u.bits array to match the length of this.bits, in which case what we actually
        //   perform is simply setting the remaining indices of this.bits to false.
        // Note that while this logic is helpful for the AND operation if we want to use this
        //   implementation (implicit padding), it is never necessary for the OR and XOR operations.
        if (this.length > u.length) {
            for (int i = u.length; i < this.length; i++) {
                this.bits[this.length - i - 1] = false;
            }
        }
    }

    /**
     * Accepts a pair of UInt objects and uses a temporary clone to safely AND them together (without changing either).
     *
     * @param a The first UInt
     * @param b The second UInt
     * @return The temp object containing the result of the AND op.
     */
    public static UInt and(UInt a, UInt b) {
        UInt temp = a.clone();
        temp.and(b);
        return temp;
    }

    public void or(UInt u) {
        for (int i = 0; i < Math.min(this.length, u.length); i++) {
            this.bits[this.length - i - 1] =
                    this.bits[this.length - i - 1] ||
                            u.bits[u.length - i - 1];
        }
    }

    public static UInt or(UInt a, UInt b) {
        UInt temp = a.clone();
        temp.or(b);
        return temp;
    }

    public void xor(UInt u) {
        for (int i = 0; i < Math.min(this.length, u.length); i++) {
            this.bits[this.length - i - 1] =
                    this.bits[this.length - i - 1] ^
                            u.bits[u.length - i - 1];
        }
    }

    public static UInt xor(UInt a, UInt b) {
        UInt temp = a.clone();
        temp.xor(b);
        return temp;
    }

    public void add(UInt u) {
        // TODO Using a ripple-carry adder, perform addition using a passed UINT object
        // The result will be stored in this.bits
        // You will likely need to create a couple of helper methods for this.
        // Note this one, like the bitwise ops, also needs to be aligned on the 1s place.
        // Also note this may require increasing the length of this.bits to contain the result.

        // sum stores the sum of this.bits and u.bits
        // its length is the largest len between this and u (+1 for possible carry bit overflow)
        int trueCount = 0;
        int diffInLengths;
        int maxLength = (Math.max(this.length, u.length)) + 1;
        boolean[] sum = new boolean[maxLength];
        boolean carry = false;
        UInt uTemp = u.clone(); // avoid altering 'u' directly

        // pad the shorter array with leading false(s)/zeroes
        if(this.length > u.length) {
            diffInLengths = this.length - u.length;
            uTemp.padWithLeadingZeroes(diffInLengths);
        }
        else { // (this.length < u.length)
            diffInLengths = u.length - this.length;
            this.padWithLeadingZeroes(diffInLengths);
        }

        for(int i = 0; i < maxLength - 1; i++) {
            //
            sum[maxLength - i - 1] = uTemp.bits[uTemp.length - i - 1] ^ this.bits[this.length - i - 1] ^ carry;

            // for each true value, count + 1
            trueCount = 0;
            trueCount = (uTemp.bits[uTemp.length - i - 1] ? 1 : 0) +
                    (this.bits[uTemp.length - i - 1] ? 1 : 0) +
                    (carry ? 1 : 0);

            // if > 1 value is true, next carry is true
            carry = trueCount > 1;
        }

        // handle possible last carry bit
        if(carry) {
            sum[1] = true; // in index [1] b/c [0] should be a leading false/0
        }

        // this.bits = new boolean[maxLength];
        this.length = maxLength;
        this.bits = sum;
    }

    public static UInt add(UInt a, UInt b) {
        UInt temp = a.clone();
        temp.add(b);
        return temp;
    }

    // same as add() method but ignores final carry bit (for mul() & sub() method)
    public void add_IgnoreFinalCarry(UInt u) {
        int trueCount = 0;
        int diffInLengths;
        int maxLength = (Math.max(this.length, u.length));
        boolean[] sum = new boolean[maxLength];
        boolean carry = false;

        // pad the shorter array with leading false(s)/zeroes
        if(this.length > u.length) {
            diffInLengths = this.length - u.length;
            u.padWithLeadingZeroes(diffInLengths);
        }
        else { // (this.length < u.length)
            diffInLengths = u.length - this.length;
            this.padWithLeadingZeroes(diffInLengths);
        }

        for(int i = 0; i < maxLength; i++) {
            sum[maxLength - i - 1] = u.bits[u.length - i - 1] ^ this.bits[this.length - i - 1] ^ carry;

            // for each true value, count + 1
            trueCount = 0;
            trueCount += (u.bits[u.length - i - 1] ? 1 : 0);
            trueCount += (this.bits[u.length - i - 1] ? 1 : 0);
            trueCount += (carry ? 1 : 0);

            // if > 1 value is true, next carry is true
            carry = trueCount > 1;
        }

        // this.bits = new boolean[maxLength];
        this.length = maxLength;
        this.bits = sum;
    }

    public static UInt add_IgnoreFinalCarry(UInt a, UInt b) {
        UInt temp = a.clone();
        temp.add_IgnoreFinalCarry(b);
        return temp;
    }

    public void negate() {
        // TODO You'll need a way to perform 2's complement negation
        // The add() method will be helpful with this.

        int firstTrueIndex;

        // Loop through this.bits array, Every index = !index (flip all the bits/negate)
        for(int i = 0; i < this.length; i++) {
            this.bits[i] = !this.bits[i];
        }

        // Add 1 using add() method to satisfy 2's complement
        this.add_IgnoreFinalCarry(new UInt(1));
    }

    public void sub(UInt u) {
        // TODO Using negate() and add(), perform in-place subtraction
        // As this class is supposed to handle only unsigned values,
        //   if the result of the subtraction operation would be a negative number then it should be coerced to 0.
        int thisInt = this.toInt();
        int uInt = u.toInt();
        int diffInLengths;
        UInt tempU = u.clone();

        // pad the shorter array with leading false(s)/zeroes
        if(this.length > u.length) {
            diffInLengths = this.length - u.length;
            tempU.padWithLeadingZeroes(diffInLengths);
        }
        else { // (this.length < u.length)
            diffInLengths = u.length - this.length;
            this.padWithLeadingZeroes(diffInLengths);
        }

        tempU.negate();

        // Add the two together using add()
        this.add_IgnoreFinalCarry(tempU);

        // Coerce answer to 0 if it would be negative
        if((thisInt - uInt) < 0) {
            this.length = 1;
            this.bits[0] = false;
        }
    }

    public static UInt sub(UInt a, UInt b) {
        UInt temp = a.clone();
        temp.sub(b);
        return temp;
    }

    public void mul(UInt u) {
        // TODO Using Booth's algorithm, perform multiplication
        // This one will require that you increase the length of bits, up to a maximum of X+Y.
        // Having negate() and add() will obviously be useful here.
        // Also note the Booth's always treats binary values as if they are signed,
        //   while this class is only intended to use unsigned values.
        // This means that you may need to pad your bits array with a leading 0 if it's not already long enough.
        int diffInLengths;
        int ASP_length;
        int numCycles; // Number of booth's algorithm cycles necessary to satisfy the algorithm
        // based on length of largest binary value
        UInt m = this.clone();
        UInt m_negated = this.clone();
        m_negated.negate();
        UInt r = u.clone();

        // Compare both this. and u. lengths - whichever one is longer, pad shorter one
        // pad the shorter array with leading false(s)/zeroes
        if(m.length > r.length) {
            diffInLengths = m.length - r.length;
            r.padWithLeadingZeroes(diffInLengths);
        }
        else { // (this.length < u.length)
            diffInLengths = r.length - m.length;
            m.padWithLeadingZeroes(diffInLengths);
            m_negated.padWithLeadingZeroes(diffInLengths);
        }

        numCycles = m.length;
        ASP_length = (m.length * 2) + 1;

        UInt A = new UInt(1);
        A.length = ASP_length;
        A.bits = new boolean[ASP_length];
        System.arraycopy(m.bits, 0, A.bits, 0, numCycles);

        UInt S = new UInt(1);
        S.length = ASP_length;
        S.bits = new boolean[ASP_length];
        System.arraycopy(m_negated.bits, 0, S.bits, 0, numCycles);

        UInt P = new UInt(1);
        P.length = ASP_length;
        P.bits = new boolean[ASP_length];
        System.arraycopy(r.bits, 0, P.bits, ASP_length - numCycles - 1, numCycles);

        int lastBit = ASP_length - 1;
        int secondToLastBit = ASP_length - 2;

        for(int i = 0; i < numCycles; i++) {
            // check last 2 bits of current P, 01 -> add A, 10 -> add S. 11 or 00 -> nothing
            if(P.bits[secondToLastBit] && !P.bits[lastBit]) {
                P.add_IgnoreFinalCarry(S);
            }
            else if(!P.bits[secondToLastBit] && P.bits[lastBit]) {
                P.add_IgnoreFinalCarry(A);
            }
            // final step of each cycle is an arithmetic shift right
            P.arithmeticShiftRight();
        }
        // Ignore the very last bit per Booth's algorithm rules
        boolean[] product = new boolean[ASP_length];
        System.arraycopy(P.bits, 0, product, 1, ASP_length - 1);

        this.length = ASP_length;
        this.bits = product;
    }

    public static UInt mul(UInt a, UInt b) {
        UInt temp = a.clone();
        temp.mul(b);
        return temp;
    }

    public void padWithLeadingZeroes(int numZeroes) {
        int newLen = this.length + numZeroes;
        boolean[] paddedBits = new boolean[newLen];

        System.arraycopy(this.bits, 0, paddedBits, numZeroes, this.length);

        this.length = newLen;
        this.bits = paddedBits;
    }

    public void arithmeticShiftRight() {
        // an arithmetic shift right maintains the sign bit with the shift
        // necessary for mul() method to follow Booth's algorithm

        // current index = previous index
        for(int i = this.length - 1; i > 0; i--) {
            this.bits[i] = this.bits[i - 1];
        }

        // preserve value at original index 0
        this.bits[0] = this.bits[1];
    }
}
