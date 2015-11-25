import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import javax.imageio.ImageIO;

import fr.apteryx.imageio.dicom.DataSet;
import fr.apteryx.imageio.dicom.DicomReader;
import fr.apteryx.imageio.dicom.FileSet;
import fr.apteryx.imageio.dicom.Tag;
import fr.apteryx.imageio.dicom.Plugin;

public class ReadDicomDir {

    Vector atributosExames, frameTime;
    Vector<Object> filesExames;


    public ReadDicomDir() {
        filesExames = new Vector();
        atributosExames = new Vector();
        frameTime = new Vector();

        Plugin.setLicenseKey("NM73KIZUPKHLFLAQM5L0V9U");
    }

    public Vector leDirectorio(String path, Vector atributosExames) throws Exception {
        path = getPathFormat(path);

        Vector result = new Vector();

        ImageIO.scanForPlugins();
        Iterator<?> read = ImageIO.getImageReadersByFormatName("dicom");
        DicomReader dicomReader = (DicomReader) read.next();

        FileSet fileSet = new FileSet(new File(path), dicomReader);

        FileSet.Directory rootDirectory = fileSet.getRootDirectory();

        for (int i = 0; i < rootDirectory.getNumRecords(); i++) {
            FileSet.Record record = rootDirectory.getRecord(i);

            if (record.getType().equals("PATIENT")) {
                DataSet patient = record.getAttributes();

                String patientName = record.getAttribute(Tag.PatientsName).toString();
                String patientId = record.getAttribute(Tag.PatientID).toString();
                String patientBirth = record.getAttribute(Tag.PatientsBirthDate).toString();

                for (int i1 = 0; i1 < record.getLowerLevelDirectory().getNumRecords(); i1++) {
                    FileSet.Record record1 = record.getLowerLevelDirectory().getRecord(i);

                    if (record1.getType().equals("STUDY")) {
                        DataSet study = record1.getAttributes();

                        for (int i2 = 0; i2 < record1.getLowerLevelDirectory().getNumRecords(); i2++) {
                            FileSet.Record record2 = record1.getLowerLevelDirectory().getRecord(i2);

                            if (record2.getType().equals("SERIES")) {
                                DataSet series = record2.getAttributes();

                                String modality = record2.getAttribute(Tag.Modality).toString();

                                for (int i3 = 0; i3 < record2.getLowerLevelDirectory().getNumRecords(); i3++) {
                                    FileSet.Record record3 = record2.getLowerLevelDirectory().getRecord(i3);

                                    if (record3.getType().equals("IMAGE")) {
                                        DataSet image = record3.getAttributes();

                                        Vector temp = new Vector();
                                        temp.add(modality);
                                        temp.add(patientId);
                                        temp.add(patientBirth);
                                        temp.add(patientName);

                                        result.add(temp);

                                        Atributes atributes = new Atributes(patient, study, series, image);
                                        atributosExames.add(atributes);

                                        filesExames.add(record3.getAttribute(Tag.ReferencedFileID));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    private String getPathFormat(String path) {
        String DICOMDIR = "DICOMDIR";

        if (path.contains(DICOMDIR)) {
            return path;
        } else {
            return path + "/" + DICOMDIR;
        }
    }

    public Vector getFilesExames() {
        return filesExames;
    }
}
