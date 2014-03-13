		aseg
		org 3000h
comparaZonas	macro
		ld (aux), A
		ld A, (IX)
		cp (IY)
		ld A, (aux)
		jr aux+1
aux:		ds 1
		endm
start:		ld IX, numero
		ld IY, numero+largo-1
		ld A, 0
		ld B, largo/2
ciclo:		comparaZonas
		jr NZ, distintos
		inc IX
		dec IY
		djnz ciclo
		inc A
distintos:	ld (respuesta), A
		rst 38h
numero:		db '2','1','4','6','6','4','1','2'
largo:		equ $-numero
respuesta:	ds 1
		end start
