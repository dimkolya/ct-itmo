import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RISCVDisassembler {
    private static class Header {
        private final int e_entry;
        private final int e_phoff;
        private final int e_shoff;
        private final int e_flags;
        private final int e_ehsize;
        private final int e_phentsize;
        private final int e_phnum;
        private final int e_shentsize;
        private final int e_shnum;
        private final int e_shstrndx;

        public Header(
            int e_entry,
            int e_phoff,
            int e_shoff,
            int e_flags,
            int e_ehsize,
            int e_phentsize,
            int e_phnum,
            int e_shentsize,
            int e_shnum,
            int e_shstrndx) {
            this.e_entry = e_entry;
            this.e_phoff = e_phoff;
            this.e_shoff = e_shoff;
            this.e_flags = e_flags;
            this.e_ehsize = e_ehsize;
            this.e_phentsize = e_phentsize;
            this.e_phnum = e_phnum;
            this.e_shentsize = e_shentsize;
            this.e_shnum = e_shnum;
            this.e_shstrndx = e_shstrndx;
        }

        public int getE_entry() {
            return e_entry;
        }

        public int getE_phoff() {
            return e_phoff;
        }

        public int getE_shoff() {
            return e_shoff;
        }

        public int getE_flags() {
            return e_flags;
        }

        public int getE_ehsize() {
            return e_ehsize;
        }

        public int getE_phentsize() {
            return e_phentsize;
        }

        public int getE_phnum() {
            return e_phnum;
        }

        public int getE_shentsize() {
            return e_shentsize;
        }

        public int getE_shnum() {
            return e_shnum;
        }

        public int getE_shstrndx() {
            return e_shstrndx;
        }
    }


    private static class SectionHeader {
        private int sh_name;
        private int sh_type;
        private int sh_flags;
        private int sh_addr;
        private int sh_offset;
        private int sh_size;
        private int sh_link;
        private int sh_info;
        private int sh_addralign;
        private int sh_entsize;

        public SectionHeader(int sh_name,
                             int sh_type,
                             int sh_flags,
                             int sh_addr,
                             int sh_offset,
                             int sh_size,
                             int sh_link,
                             int sh_info,
                             int sh_addralign,
                             int sh_entsize) {
            this.sh_name = sh_name;
            this.sh_type = sh_type;
            this.sh_flags = sh_flags;
            this.sh_addr = sh_addr;
            this.sh_offset = sh_offset;
            this.sh_size = sh_size;
            this.sh_link = sh_link;
            this.sh_info = sh_info;
            this.sh_addralign = sh_addralign;
            this.sh_entsize = sh_entsize;
        }

        public int getSh_name() {
            return sh_name;
        }

        public int getSh_type() {
            return sh_type;
        }

        public int getSh_flags() {
            return sh_flags;
        }

        public int getSh_addr() {
            return sh_addr;
        }

        public int getSh_offset() {
            return sh_offset;
        }

        public int getSh_size() {
            return sh_size;
        }

        public int getSh_link() {
            return sh_link;
        }

        public int getSh_info() {
            return sh_info;
        }

        public int getSh_addralign() {
            return sh_addralign;
        }

        public int getSh_entsize() {
            return sh_entsize;
        }
    }

    private static Header header;
    private static SectionHeader[] sectionHeaders;
    private static String[] sectionNames;
    private static Map<Integer, String> nameByAddress = new HashMap<>();

    private static String strtabData;

    private static boolean parseHeader(byte[] buffer) {
        if (buffer[0] != 0x7f || buffer[1] != 0x45 || buffer[2] != 0x4c || buffer[3] != 0x46) {
            System.err.println("    This file isn't ELF");
            return false;
        }
        if (buffer[4] != 1) {
            System.err.println("    This elf file isn't 32 bit");
            return false;
        }
        if (buffer[5] != 1) {
            System.err.println("    Encoding of this elf file isn't little endian");
            return false;
        }
        ByteBuffer bb = ByteBuffer.wrap(buffer, 24, 28);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        header = new Header(
                bb.getInt(),
                bb.getInt(),
                bb.getInt(),
                bb.getInt(),
                bb.getShort(),
                bb.getShort(),
                bb.getShort(),
                bb.getShort(),
                bb.getShort(),
                bb.getShort()
        );
        sectionHeaders = new SectionHeader[header.getE_shnum()];
        sectionNames = new String[header.getE_shnum()];
        return true;
    }

    private static boolean parseSectionHeader(byte[] buffer, int i) {
        ByteBuffer bb = ByteBuffer.wrap(buffer,
                header.getE_shoff() + i * header.getE_shentsize(),
                header.getE_shentsize());
        bb.order(ByteOrder.LITTLE_ENDIAN);
        sectionHeaders[i] = new SectionHeader(
                bb.getInt(),
                bb.getInt(),
                bb.getInt(),
                bb.getInt(),
                bb.getInt(),
                bb.getInt(),
                bb.getInt(),
                bb.getInt(),
                bb.getInt(),
                bb.getInt()
        );
        return true;
    }

    private static int findSection(String name) {
        for (int i = 0; i < header.getE_shnum(); i++) {
            if (sectionNames[i].equals(name)) {
                return i;
            }
        }
        return -1;
    }

    private static String addZeros(String s, int n) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < n - s.length(); i++) {
            result.append('0');
        }
        result.append(s);
        return result.toString();
    }

    private static String addSpaces(String s, int n) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < n - s.length(); i++) {
            result.append(' ');
        }
        result.append(s);
        return result.toString();
    }

    private static void parse32(int codeInt, int address, BufferedWriter writer) throws IOException {
        String code = addZeros(Integer.toBinaryString(codeInt), 32);
        String opcode = code.substring(25, 32);
        String command = "unknown_command", rd = null, imm = null, funct3 = null;
        switch (opcode) {
            case "0110111":
                command = "LUI";
                rd = code.substring(20, 25);
                imm = code.substring(0, 20);
                break;
            case "0010111":
                command = "AUIPC";
                rd = code.substring(20, 25);
                imm = code.substring(0, 20);
                break;
            case "1101111":
                command = "JAL";
                break;
            case "1100111":
                command = "JALR";
                break;
            case "1100011":
                funct3 = code.substring(17, 20);
                switch (funct3) {
                    case "000":
                        command = "BEQ";
                        break;
                    case "001":
                        command = "BLT";
                        break;
                    case "101":
                        command = "BGE";
                        break;
                    case "110":
                        command = "BLTU";
                        break;
                    case "111":
                        command = "BGEU";
                        break;
                }
                break;
            case "0000011":
                funct3 = code.substring(17, 20);
                switch (funct3) {
                    case "000":
                        command = "LB";
                        break;
                    case "001":
                        command = "LH";
                        break;
                    case "010":
                        command = "LW";
                        break;
                    case "100":
                        command = "LBU";
                        break;
                    case "101":
                        command = "LHU";
                        break;
                }
                break;
            case "0100011":
                funct3 = code.substring(17, 20);
                switch (funct3) {
                    case "000":
                        command = "SB";
                        break;
                    case "001":
                        command = "SH";
                        break;
                    case "010":
                        command = "SW";
                        break;
                }
                break;
            case "0010011":
                funct3 = code.substring(17, 20);
                switch (funct3) {
                    case "000":
                        command = "ADDI";
                        break;
                    case "010":
                        command = "SLTI";
                        break;
                    case "011":
                        command = "SLTIU";
                        break;
                    case "100":
                        command = "XORI";
                        break;
                    case "110":
                        command = "ORI";
                        break;
                    case "111":
                        command = "ANDI";
                        break;
                    case "001":
                        command = "SLLI";
                        break;
                    case "101":
                        if (code.substring(0, 7).equals("0000000")) {
                            command = "SRLI";
                        } else if (code.substring(0, 7).equals("0100000")) {
                            command = "SRAI";
                        }
                        break;
                }
                break;
            case "0110011":
                funct3 = code.substring(17, 20);
                String funct7 = code.substring(0, 7);
                switch (funct3) {
                    case "000":
                        if (funct7.equals("0000000")) {
                            command = "ADD";
                        } else if (funct7.equals("0100000")) {
                            command = "SUB";
                        }
                        break;
                    case "001":
                        if (funct7.equals("0000000")) {
                            command = "SLL";
                        }
                        break;
                    case "010":
                        if (funct7.equals("0000000")) {
                            command = "SLT";
                        }
                        break;
                    case "011":
                        if (funct7.equals("0000000")) {
                            command = "SLTU";
                        }
                        break;
                    case "100":
                        if (funct7.equals("0000000")) {
                            command = "XOR";
                        }
                        break;
                    case "101":
                        if (funct7.equals("0000000")) {
                            command = "SRL";
                        } else if (funct7.equals("0100000")) {
                            command = "SRA";
                        }
                        break;
                    case "110":
                        command = "OR";
                        break;
                    case "111":
                        command = "AND";
                        break;
                }
                break;
            case "0001111":
                if (code.substring(0, 7).equals("0000000")) {
                    command = "FENCE";
                }
                break;
            case "1110011":
                if (code.substring(0, 12).equals("000000000000")) {
                    command = "ECALL";
                } else if ((code.substring(0, 12).equals("000000000001"))) {
                    command = "EBREAK";
                }
                break;
        }
        writer.write(addSpaces(Integer.toHexString(address), 8));
        writer.write(":\t");
        writer.write(addZeros(Integer.toHexString(codeInt), 8));
        writer.write("\t\t");
        writer.write(command);
        writer.write(System.lineSeparator());
    }

    private static void parse16(int codeShort, int address, BufferedWriter writer) throws IOException {
        String code = addZeros(Integer.toBinaryString(codeShort + Short.MAX_VALUE), 16);
        writer.write(addSpaces(Integer.toHexString(address), 8));
        writer.write(":\t");
        writer.write(addZeros(Integer.toHexString(codeShort + Short.MAX_VALUE), 4));
        writer.write("\t\t");
        writer.write(System.lineSeparator());
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("    File name wasn't entered");
            return;
        }
        try (FileInputStream stream = new FileInputStream(args[0])) {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), "utf8"))) {
                byte[] buffer = new byte[1];
                int bufferSize = 0;
                int temp;
                while ((temp = stream.read()) != -1) {
                    buffer[bufferSize++] = (byte) temp;
                    if (bufferSize == buffer.length) {
                        buffer = Arrays.copyOf(buffer, bufferSize * 2);
                    }
                }

                if (!parseHeader(buffer)) {
                    return;
                }
                for (int i = 0; i < header.getE_shnum(); i++) {
                    if (!parseSectionHeader(buffer, i)) {
                        return;
                    }
                }
                int sectionNames_offset = sectionHeaders[header.getE_shstrndx()].getSh_offset();
                int sectionNames_size = sectionHeaders[header.getE_shstrndx()].getSh_size();
                int sectionNames_iterator = sectionNames_offset;
                ByteBuffer bb = ByteBuffer.wrap(buffer,
                        sectionNames_offset,
                        sectionNames_size);
                bb.order(ByteOrder.LITTLE_ENDIAN);
                String s = StandardCharsets.UTF_8.decode(bb).toString();
                for (int i = 0; i < header.getE_shnum(); i++) {
                    int begin = sectionHeaders[i].getSh_name();
                    int it = begin;
                    while (it != s.length() && s.charAt(it) != '\0') {
                        it++;
                    }
                    sectionNames[i] = s.substring(begin, it);
                }

                int strtabPos = findSection(".strtab");
                if (strtabPos == -1) {
                    System.err.println("    Section .strtab doesn't exist in this ELF file");
                    return;
                }
                strtabData = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(buffer,
                        sectionHeaders[strtabPos].getSh_offset(),
                        sectionHeaders[strtabPos].getSh_size()).order(ByteOrder.LITTLE_ENDIAN)).toString();

                int symtabPos = findSection(".symtab");
                if (symtabPos == -1) {
                    System.err.println("    Section .symtab doesn't exist in this ELF file");
                    return;
                }
                int symtab_offset = sectionHeaders[symtabPos].getSh_offset();
                int symtab_size = sectionHeaders[symtabPos].getSh_size();
                int symtab_iterator = symtab_offset;
                bb = ByteBuffer.wrap(buffer, symtab_offset, symtab_size);
                bb.order(ByteOrder.LITTLE_ENDIAN);
                while (symtab_iterator != symtab_offset + symtab_size) {
                    symtab_iterator += 16;
                    final int st_name = bb.getInt();
                    final int st_value = bb.getInt();
                    final int st_size = bb.getInt();
                    final byte st_info = bb.get();
                    final byte st_other = bb.get();
                    final short st_shndx = bb.getShort();
                    if (st_name == 0) {
                        continue;
                    }
                    int begin = st_name;
                    int it = begin;
                    while (strtabData.charAt(it) != '\0') {
                        it++;
                    }
                    nameByAddress.put(st_value, strtabData.substring(begin, it));
                }

                int textPos = findSection(".text");
                if (textPos == -1) {
                    System.err.println("    Section .text doesn't exist in this ELF file");
                    return;
                }
                writer.write(System.lineSeparator());
                writer.write(args[0]);
                writer.write(":\tfile format elf32-littleriscv");
                writer.write(System.lineSeparator());
                writer.write(System.lineSeparator());
                writer.write(System.lineSeparator());
                writer.write("Disassembly of section .text:");
                writer.write(System.lineSeparator());

                int text_address = sectionHeaders[textPos].getSh_addr();
                int text_offset = sectionHeaders[textPos].getSh_offset();
                int text_size = sectionHeaders[textPos].getSh_size();
                int text_iterator = text_offset;
                bb = ByteBuffer.wrap(buffer, text_offset, text_size);
                bb.order(ByteOrder.LITTLE_ENDIAN);

                while (text_iterator < text_offset + text_size) {
                    String name = nameByAddress.get(text_address + text_iterator - text_offset);
                    if (name != null) {
                        writer.write(System.lineSeparator());
                        writer.write(addZeros(Integer.toHexString(text_address), 8));
                        writer.write(" <");
                        writer.write(name);
                        writer.write('>');
                        writer.write(System.lineSeparator());
                    }

                    int codeShort = bb.getShort();
                    text_iterator += 2;
                    if (codeShort % 4 == 0b11) {
                        int codeInt = codeShort + bb.getShort() * 65536;
                        text_iterator += 2;
                        parse32(codeInt, text_address + text_iterator - text_offset, writer);
                    } else {
                        parse16(codeShort, text_address + text_iterator - text_offset, writer);
                    }
                }
            } catch (FileNotFoundException e) {
                System.err.println("    Output file doesn't exist: " + e.getMessage());
            } catch (java.io.IOException e) {
                System.err.println("    Output error: " + e.getMessage());
            }
        } catch (FileNotFoundException e) {
            System.err.println("    Input file doesn't exist: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("    Input error: " + e.getMessage());
        }
    }
}
