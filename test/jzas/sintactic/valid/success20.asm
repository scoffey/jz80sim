		aseg
		org 1FD0h
start:		ld IX, 2000h
		ld IY, 2100h
		ld B, 0
ciclo:		ld A, (IX)
		cp (IY)
		jr NZ, distintos
		cp 0
		jr Z, iguales
		inc IX
		inc IY
		jr ciclo
distintos:	add A, 0
		jr fin
iguales:	ld A, 80h
		add A, 80h
fin:		rst 38h
		end start
