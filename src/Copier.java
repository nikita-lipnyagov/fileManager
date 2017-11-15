import java.io.*;

public class Copier implements Runnable {

    private File fileFrom;
    private File fileWhere;
    private byte buffer[];

    public Copier(byte[] buffer, File fileFrom, File fileWhere) throws FileNotFoundException {
        this.buffer = buffer;
        this.fileFrom = fileFrom;
        this.fileWhere = fileWhere;
    }


    private void calculatebytesWrite(RandomAccessFile out, int byteCount, int bytesRead) throws IOException {
        out.seek(bytesRead);
        out.write(buffer, 0, byteCount);
    }
    @Override
    public void run() {
        try(RandomAccessFile in = new RandomAccessFile(fileFrom.getPath(), "r")) {
            int byteCount;
            RandomAccessFile out;
            if(fileWhere.isDirectory()){
                out  = new RandomAccessFile(fileWhere.getPath()+"\\"+fileFrom.getName(),"rw");
            }else{
                out = new RandomAccessFile(fileWhere.getPath(),"rw");
            }
            int bytesRead=buffer.length*(-1);// умножаем на -1 чтобы при первой записи не было отступа
            while ((byteCount = in.read(buffer)) > -1) {
                synchronized (this){
                    bytesRead+=buffer.length;
                }
                calculatebytesWrite(out, byteCount,bytesRead);
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
