; Ejercicio 15 del TP 2.
; Decrementa C tantas veces como indica B.

		aseg
		org	1400h
start:	
		
		ld	A,0
		cp 	B
		jp	Z,sale		; chequea que B sea distinto de cero

ciclo:		
		dec	C
		dec	B
		cp	B
		jp	NZ,ciclo	

sale:		
		rst  	38h	           
		end  	start

