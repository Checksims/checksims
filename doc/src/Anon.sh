#!/bin/bash
# This script has ABSOLUTELY NO ERROR CHECKING
# But it shouldn't overwrite anything in typical case

student=0
srcdir="$1"
dstdir="$2"

mkdir -p "$dstdir"

find "$srcdir/students" "$srcdir/groups" -mindepth 1 -maxdepth 1 -type d -not -path "$srcdir/students" -not -path "$srcdir/groups" -print0 | while read -d $'\0' dir
do
  studentName=`basename "$dir"`
  echo "Anonymizing student $studentName"
  curDstDir="$dstdir/student_$student"
  # Make output directory
  mkdir -p "$curDstDir"
  # Increment student number
  student=$((student+1))
  
  curDir=`pwd`

  # Unzip anything we find. Ignore errors that might occur because there are no zip files.
  find "$dir" -type f -name '*.zip' -print0 | while read -d $'\0' zip
  do
    dirName=`dirname "$zip"`
    echo "Unzipping $zip to $dirName"
    unzip -o -d "$dirName" "$zip"
  done

  # Untar any tars
  find "$dir" -type f -name '*.tar' -print0 | while read -d $'\0' tar
  do
    dirName=`dirname "$tar"`
    cd "$dirName"
    echo "Untarring $tar to $dirName"
    tar -xf "$tar"
  done

  cd "$curDir"

  # Loop through all .c and .h files in that directory and anonymize them
  find "$dir" \( \( -type f -name '*.c' \) -or \( -type f -name '*.h' \) -or \( -type f -name '*.cpp' \) -or \( -type f -name '*.hpp' \) \)  -print0 | while read -d $'\0' file
  do
    # Run strip_comments script from GNU folks. Pipe output into output file.
    fileBasename=`basename "$file"`
    echo "Stripping comments from $file, outputting to $fileBasename"
    ./strip_comments.sed "$file" > "$curDstDir/$fileBasename"
  done
done

echo "Done!"
