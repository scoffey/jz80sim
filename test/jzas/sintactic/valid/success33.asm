; Ejercicio 10 del TP 2.

		aseg
		org	1FE0h
start:		
		ld	B,0
		
		ld	A, (2000h)	
		add	A,B
		ld	B, A
		
		ld	A, (2001h)	
		add	A,B
		ld	B, A
		
		ld	A, (2002h)	
		add	A,B
		ld	B, A

		rst  	38h			      
		end  	start



