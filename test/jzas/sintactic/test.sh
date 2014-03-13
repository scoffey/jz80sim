#!/bin/bash

echo "Test de analisis sintactico de archivos invalidos...";
for i in `find | grep fail | grep -v .svn` 
do
	echo $i
	(java -jar ../../../jzas.jar -S $i 2>&1) > output.txt
	
	variable=`cat output.txt | wc -c`;

	if [ "$variable" = "0" ]; then
		echo -e "ERROR"
		echo $i;
		echo -e "\n";
	fi

	excepciones=`cat output.txt | grep Exception`;
	if [ "$excepciones" != "" ]; then
		echo -e "ERROR"
		echo $i;
		cat output.txt;
		echo -e "\n";
	fi

	sleep 0.01;
done

echo "Test de analisis sintactico de archivos validos...";
for i in `find | grep success | grep -v .svn` 
do
	echo $i
	(java -jar ../../../jzas.jar -S $i 2>&1) > output.txt

	variable=`cat output.txt | wc -c`;

	if [ "$variable" != "0" ]; then
		echo -e "ERROR"
		echo $i;
		cat output.txt;
		echo -e "\n";
	fi

	sleep 0.01;
done

rm output.txt
