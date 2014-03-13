; Ejercicio 16 del TP2

		aseg
		org	1180h
start:	
	
		ld	A,(1200h)
		ld	B,A
		ld	A,(1201h)
		cp 	B
		jp	Z,iguales   ; compara primero y segundo

		ld	A,(1200h)
		ld	B,A
		ld	A,(1202h)
		cp 	B
		jp	Z,iguales  ; compara primero y tercero

		ld	A,(1201h)
		ld	B,A
		ld	A,(1202h)
		cp 	B
		jp	Z,iguales  ; compara segundo y tercero

		ld	A,80h
		jp	sale
iguales:		
		ld	A,0

sale:		
		add	A,A

		rst  	38h			; halt           
		end  	start

