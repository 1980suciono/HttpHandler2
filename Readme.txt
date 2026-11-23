// install gradle-8.6 pada jdk jdk-21.0.6 atau yang sesuai
 
//kalo menggunakan .gradle (groovy) tanpa wrapper
perintah CMD
1. gradle run, Buka Command Prompt di folder utama proyek HttpHandler2, lalu jalankan perintah "gradle run", sehingga muncul folder [Build] dan [.gradle]
2. gradle installDist, 
   Hasilnya: 1. Folder distribusi lengkap beserta file .bat untuk menjalankannya akan dibuat di:HttpHandler2\build\install\HttpHandler2\ (atau di dalam folder build/distributions/ untuk file .zip).
			 2. file HttpHandler2.jar di folder HttpHandler2\build\libs\

//kalo menggunakan .gradle (groovy) menggunakan wrapper (mendownload Gradle Wrapper agar project bisa dipindah ke komputer lain tanpa install Gradle)
1. gradle wrapper,
	Hasilnya: 1. terbentu folder [gradle], file gradlew,  file gradlew.bat
2. gradlew run atau gradlew build, (di windows) lainnya adalah ./gradlew run atau ./gradlew build, (di linux)

3. gradlew build --refresh-dependencies, kalo mulai ulang dan kemudian kembali ke langkah 1. gradle wrapper dst

//struktur project
HttpHandler2\
│
├── .gradle/                  <-- (Folder cache internal Gradle, otomatis dibuat)
├── build/                    <-- (Folder hasil kompilasi dan build)
│   ├── classes/
│   ├── libs/                 <-- Tempat file .jar hasil build disimpan
│   └── install/              <-- Tempat hasil build aplikasi (jika pakai installDist)
│
├── gradle/                   <-- (Folder pendukung Gradle Wrapper)
│   └── wrapper/
│
├── libs/
├── src/                      <-- Folder utama kode sumber project
│   └── main/
│       ├── java/
│       │   └── com 
│       │       └── indolibs 
│       │           └── libs
│       │               └── HttpHandler2.java    <-- File kelas untuk logika & utilitas
│       │
│       └── resources/        <-- (Opsional: untuk file config, gambar, dll)
│
├── .gitignore                <-- (Opsional: konfigurasi file yang di-ignore Git) //gak ada
├── build.gradle              <-- File konfigurasi utama Gradle (plugins, dependencies)
├── gradlew                   <-- Gradle Wrapper untuk Mac / Linux
├── gradlew.bat               <-- Gradle Wrapper untuk Windows
├── settings.gradle           <-- File pengaturan nama project
└── manifest.txt              <-- (File manifest kustom yang sempat Anda buat) //kalo pake gradle gak perlu krn dibuatkan otomatis di file .jar


//untuk library yg di upload ke repository github
HttpHandler2\
│
├── src/ (Semua kode Java Anda)
│   └── main/
│       ├── java/
│       │   └── com 
│       │       └── indolibs 
│       │           └── libs
│       │               └── HttpHandler2.java    <-- File kelas untuk logika & utilitas
│       │
│       └── resources/ 
├── gradle/ (Folder wrapper Gradle)
│   └── wrapper/
├── libs/ (Jika Anda meletakkan file .jar eksternal di sini yang dibutuhkan oleh library Anda)
├── build.gradle (File konfigurasi untuk library)
├── settings.gradle
├── gradlew (Gradle Wrapper Linux)
├── gradlew.bat (Gradle Wrapper windows)
