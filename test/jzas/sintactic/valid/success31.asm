		aseg
		org 1FD0h
start:		ld IX, 2000h
		ld IY, 2100h
		ld B, 0
ciclo:		ld A, (IX)
		cp (IY)
		jr NZ, distintos
		cp 0
		jr Z, prefijo
		inc IX
		inc IY
		jr ciclo
distintos:	cp 0
		jr Z, prefijo
		ld A, (IY)
		cp 0
		jr Z, prefijo
		add A, 0
		jr fin
prefijo:	ld A, 80h
		add A, 80h
fin:		rst 38h
		end start
