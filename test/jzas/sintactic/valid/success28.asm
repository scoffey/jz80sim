; Ejercicio 18 del TP 2.
; Obtiene el campo E de un numero IEEE representado a partir de .

		aseg
		org	14A0h
start:	
	
		ld	A,(1500h)
		add	A		; multiplico por 2 para desplazar
		ld	B,A		; guardo A
		ld	A,(1500h)
		add	80h
		jp	NC,sale		; obtengo el 8vo bit del E
		inc	B
sale:		
		ld	A,B
		
	
		rst  	38h			; halt           
		end  	start

