		aseg
		org 3000h

cociente	macro dividendo, divisor, zona
		ld (aux), A
		ld A, B
		ld (aux+1), A		; resguardo A y B
		ld A, divisor
		ld B, 0			; inicializo A y B
ciclo:		cp dividendo
		jp P, listo		; si llegue a dividendo, salgo del ciclo
		add A, divisor		; sumo otra vez divisor
		inc B			; cuento que sume otra vez divisor
		jr ciclo
listo:		ld A, B
		ld zona, A		; cargo lo que habia quedado en B en zona
		ld A, (aux+1)		; restauro A y B
		ld B, A
		ld A, (aux)
		jr aux+2
aux:		ds 2
		endm

intercambia	macro zona1, zona2
		ld (aux), A	; resguardo A
		ld A, (zona1)
		ld (aux+1), A	; zona1 para a aux+1
		ld A, (zona2)
		ld (zona1), A	; zona2 pasa a zona1
		ld A, (aux+1)
		ld (zona2), A	; aux+1 (zona1) pasa a zona2
		ld A, (aux)	; restauro A
		jr aux+2
aux:		ds 2
		endm

start:		ld A, N		; cargo el numero N en A
		ld B, iteraciones+1	; B es iterador
		ld C, 0		; C es un registro auxiliar para el calculo de la formula recursiva
		srl A		; el primer termino es A/2
		jp djnz1
ciclo:		cociente N,A,C	; C = N/A
		add A, C	; A = A+C
		srl A		; A = A/2
djnz1:		djnz ciclo
		ld (respuesta), A
		rst 38h

N:		equ 17
iteraciones:	equ 3
respuesta:	ds 1
		end start
