## Program that parse directory with files that passed as args and form statistics from files data. 

### Since there were no additional requirements, we assume(for simplicity), that:
1) Program has permissions read/write into PC/notepad/macbook files/directories.
2) Program is used in Intellij Idea/some other IDE, not .jar (because output directory and some others that is used for creating/reading files is hardcoded and tied to a project structure. Resulting file output directory = "src/main/resources")
3) Program will be used in Windows systems (although Linux-like systems should be supported too, if granted permissions for execution).
4) Input file consists explicitly of json objects with structure corresponding to "Car" class in this program.
5) Input validation only on arguments passed to a program, such as "is directory valid? is second attribute empty? etc."
6) Files could be any size, from 1kb to 100+gb, so program will read data from file/s line-by-line to avoid unnecessary RAM usage.

### How to use:
1) In file "Main" click on "run" icon(green triangle) and select "Modify run configuration".
2) In "Program arguments" field specify directory with .json files and field that you like to gather statistics. Arguments should be separated by whitespace in order to be executed properly, or validation will throw error. **Example: src/main/resources owner**
3) Output file will be placed inside src/main/resources directory, name: statistics_by_{second_arg_passed}.xml. **Example: statistics_by_brand.xml**

### Program classes:
- Main. Starting point of a program.
- FileParser. Class that responsible for file parsing and extraction required data.
- FileRemover. Class that responsible for deleting .json files.
- FileWriter. Class responsible for creating file writing info inside.
- RandomFilesWithCarsGenerator. Class responsible for .json files generation and population with data.
- FileController. Class that controls flow of a program, invokes other methods from other classes.


### Multithreading:
#### Creating files and populate with data:
* **Single thread:** 
- 54 seconds to create 5 files with 2_000_000 objects (all files weigh 373+mb)
- 40 seconds to create 7 files with 1_000_000 objects. files weigh 186+mb.
- 118 seconds to create 1 file with 20_000_000 objects (file weight 3.73gb)
- 5 seconds to create single file with 1_000_000 objects (weight 198mb).

* **Seven threads:**
- 25 seconds to create 7 files with 1_000_000 objects (186+mb). Seems like disc I/O operations limits code to be executed at some speed.
- 294 seconds for 7 threads to create 7 files, each 3.79gb weigh.

#### Reading from files:
- 5.3 seconds for 1 thread to parse 500 files each 1mb.
- 3.5 seconds for 2 threads to parse 500 files each 1mb.
- 2.4 seconds for 4 threads to parse 500 files each 1mb.
- 2 seconds for 12 threads to parse 500 files each 1mb.
- 284 seconds for 7 thread to parse 7 files each 3.79 gb

#### Write resulting statistics to file:
- 145 milliseconds to write info into file

### Additional info about tests:
1) Program will try to create "empty" and "not_empty" folders inside src->test directory and populate with test files. After test execution it should automatically delete files and folders.
2) Entities in directory "model" not tested. Because reasons? They don't have any logic, and now is only behave like temporary placeholder.
3) Main method not tested.

### Additional info:
1) **Main entity:** 
- Car (
   - CarBrand brand;
   - CarModel model;
   - Integer yearOfRelease;
   - Owner owner;
   - Integer mileage;
   - List<CarAccessories> accessories;
   - boolean wasInAccident;) 
2) **Additional entities:** 
- CarAccessories(enums(WINDOW_TINTING("window tinting"), ...)), 
- CarBrand(enums(TOYOTA, ...)), 
- CarModel(enums(COROLLA, ...)), 
- Owner(String name, String lastname). 
3) **Input example:**
- "owner":"Brad Pitt",
- "was_in_accident":false,
- "year_of_release":2023,
- "accessories":"rear seat entertaining system, GPS navigator, GPS navigator, dash cam, rear seat entertaining system, backup cameras",
- "model":"OPTIMA",
- "brand":"BUICK",
- "mileage":124072
4) **Output example:** 
- \<?xml version="1.0" encoding="UTF-8" standalone="no"?>
-  \<statistic>
- \<item>
- \<value>false</value>
- \<count>1576</count>
- \</item>
- \<item>
- \<value>true</value>
- \<count>3424</count>
- \</item>
- \</statistic>

5) There are two utility classes -> "RandomFilesWithCarsGenerator" and "FileRemover". First is used to create at specified directory some number of .json files and populate them with correct info to test program workflow with different number of threads. FileRemover deletes .json files from directory passed as 1st argument into program. **Example of usage is commented in "Main" class.**
6) "model" directory and classes inside is for future expansion, currently used only while generating files to read from.
7) In directory "resources" files with statistics(.xml) needs to be deleted manually.
