		aseg
		org 1FD0h
start:		ld IX, 2000h
		ld C, 0
ciclo:		ld A, (IX)
		cp 0
		jr Z, fin
		cp ' '
		jr NZ, sigo
		inc C
sigo:		inc IX
		jr ciclo
fin:		rst 38h
		end start
