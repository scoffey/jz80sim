		aseg
		org 1FD0h
start:		ld IX, 2000h
		ld B, 0
ciclo:		ld A, (IX)
		cp 0
		jr Z, fin
		inc B
		inc IX
		jr ciclo
fin:		rst 38h
		end start
