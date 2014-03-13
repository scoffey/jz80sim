		aseg
		org 3000h

maximo		macro num1,num2,mayor
		ld (aux), A
		ld A, (num1)
		cp (num2)
		jp P, num1esmax
		ld A, (num2)
num1esmax:	ld (mayor), A
		ld A, (aux)
		jr aux+1
aux:		ds 1
		endm

start:		ld IX, vector
		ld B, largo
ciclo:		maximo maxElem,IX,maxElem
		inc IX
		djnz ciclo
		rst 38h

vector:		db 1,3,5,7,2,4,6
largo:		equ $-vector
maxElem:	ds 1
		end start
