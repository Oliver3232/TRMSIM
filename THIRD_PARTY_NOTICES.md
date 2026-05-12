# Third-Party Notices

This project is a modified version of **TRMSim-WSN** and preserves the original
LGPL v3-or-later license text and additional attribution requirements in
`TRM/src/resources/`. See [NOTICE.md](NOTICE.md) and [LICENSE](LICENSE) for details.

---

## FlatLaf

- **Artifact**: `com.formdev:flatlaf:3.5.2`
- **License**: Apache License 2.0
- **Homepage**: https://www.formdev.com/flatlaf/
- **Source**: https://github.com/JFormDesigner/FlatLaf

Used for the modern look-and-feel of the Swing GUI.

```
Copyright 2019–2024 FormDev Software GmbH

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0
```

---

## OpenJFX (JavaFX)

- **Artifacts**: `org.openjfx:javafx-controls:21.0.5`, `org.openjfx:javafx-swing:21.0.5`
- **License**: GNU General Public License v2.0 **with Classpath Exception**
- **Homepage**: https://openjfx.io/
- **Source**: https://github.com/openjdk/jfx

Used for chart rendering components inside the simulator's graph workspace.

The Classpath Exception permits linking OpenJFX with non-GPL code (such as this
project) without requiring the linked application to adopt the GPL, as long as
the OpenJFX library itself is not modified.

```
This code is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License version 2 only, as
published by the Free Software Foundation. Oracle designates this
particular file as subject to the "Classpath" exception as provided
by Oracle in the LICENSE file that accompanied this code.
```

Full license text: https://github.com/openjdk/jfx/blob/master/LICENSE

---

## LIBSVM

- **Artifact**: `tw.edu.ntu.csie:libsvm:3.35`
- **License**: BSD 3-Clause ("Modified BSD")
- **Homepage**: https://www.csie.ntu.edu.tw/~cjlin/libsvm/
- **Source**: https://github.com/cjlin1/libsvm

Used exclusively inside the `SVMTrust` trust model (`trm/svmtrust/`) as the SVM
training and prediction engine. The simulator-facing TRMSim model API is not
derived from LIBSVM.

```
Copyright (c) 2000-2024 Chih-Chung Chang and Chih-Jen Lin
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.

3. Neither name of copyright holders nor the names of its contributors
   may be used to endorse or promote products derived from this software
   without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED.
```
