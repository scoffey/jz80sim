		aseg
		org 20D0h
start:		ld C, 0		; C contador de positivos
		ld IX, 2103h	; IX apunta al byte del signo del primer numero (considerando little endian)
		ld B, 3		; B cuenta las iteraciones por los 3 numeros
ciclo:		ld A, (IX)	; cargo el byte del signo en A
		add A, 80h	; provoca carry
		jp C, sigo	; si no hubo carry, era positivo
		inc C		; y entonces lo cuento
sigo:		inc IX		; sino sigo al siguiente numero IEEE
		inc IX		; cuyo signo esta 4 bytes mas adelante en la memoria
		inc IX
		inc IX
		djnz ciclo
		ld A, C
		rst 38h
		end start
