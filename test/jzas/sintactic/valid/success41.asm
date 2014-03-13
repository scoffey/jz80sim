		aseg
		org 3000h

swap:		ld (aux), A	;aux=A
		ld A, C
		ld (aux+1), A	;aux+1=C
		ld A, (aux)
		ld C, A		;C=aux
		ld A, (aux+1)	;A=aux+1
		jr aux+2
aux:		ds 2
		nop
		ret

start:		ld IX, notas
		ld B, largo+1	; B iterador
		ld C, 0		; C suma de notas
		ld D, 0		; D maximo
		jp djnz1
ciclo:		ld A, (IX)
		cp D
		jp M, noesmayor
		ld D, A
noesmayor:	add A, C
		ld C, A
		inc IX
djnz1:		djnz ciclo
		ld A, D
		ld (maximo), A
		ld B, 0		; B cociente (suma notas / largo)
		ld A, 0		; A suma de largo (divisor)
ciclodiv:	cp C		; compara con C suma de notas (dividendo)
		jp P, divlista
		add A, largo
		inc B
		jr ciclodiv
divlista:	dec B		; el cociente B se paso por 1
		ld A, B
		ld (promedio), A
		call swap	; solo para probar
fin:		rst 38h

notas:		db 8,2,10,2,4,4,2,5
largo:		equ $-notas
promedio:	ds 1
maximo:		ds 1
		end start
