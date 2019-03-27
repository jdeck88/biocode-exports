#!/bin/bash
echo "Start"
count=0

NewLine=`echo "\n"`
# initialize first set
mydir="set1"
printf 'parentIdentifier,fileName\n'  > $mydir/metadata.csv

while IFS=$'\t' read -r -a myArray
do
    if [[ $count -eq 10000 ]]; then
	mydir="set2" 
 	printf 'parentIdentifier,fileName\n'  > $mydir/metadata.csv
    fi
    if [[ $count -eq 20000 ]]; then
	mydir="set3" 
 	printf 'parentIdentifier,fileName\n'  > $mydir/metadata.csv
    fi
    if [[ $count -eq 30000 ]]; then
	mydir="set4" 
 	printf 'parentIdentifier,fileName\n'  > $mydir/metadata.csv
    fi
    if [[ $count -eq 40000 ]]; then
	mydir="set5" 
 	printf 'parentIdentifier,fileName\n'  > $mydir/metadata.csv
    fi
    if [[ $count -eq 50000 ]]; then
	mydir="set6" 
 	printf 'parentIdentifier,fileName\n'  > $mydir/metadata.csv
    fi
    if [[ $count -eq 60000 ]]; then
	mydir="set7" 
 	printf 'parentIdentifier,fileName\n'  > $mydir/metadata.csv
    fi
    sampleID="${myArray[0]}"
    originalURL="${myArray[1]}"
    filename="${myArray[2]}"

    # curl image file and write to output directory:
    #echo $originalURL
    #echo $sampleID 
    #echo $filename
    echo "writing $originalURL to $mydir/$filename"

    # get the filename and save to directory
    curl -s $originalURL -o $mydir/$filename

    echo "printing to $mydir/metadata.csv file"
    printf $sampleID,$filename$NewLine >> $mydir/metadata.csv

    count=$(($count+1))

done < biocode_photos_from_calphotos.txt
