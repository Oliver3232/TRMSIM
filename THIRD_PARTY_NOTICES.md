# Third-Party Notices

This project is a modified version of TRMSim-WSN and preserves the original
LGPL v3-or-later license text and additional attribution requirements in
`TRM/src/resources`.

The Maven build also uses these third-party libraries:

- FlatLaf (`com.formdev:flatlaf`) - Apache License 2.0
- OpenJFX (`org.openjfx:javafx-controls`, `org.openjfx:javafx-swing`) - GPL with Classpath Exception
- LIBSVM (`tw.edu.ntu.csie:libsvm`) - BSD 3-Clause License

LIBSVM is used only inside the `SVMTrust` trust model as the SVM training and
prediction engine. The simulator-facing TRMSim model API remains unchanged.
