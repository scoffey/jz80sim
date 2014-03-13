; Ejercicio 11 del TP 2.

		aseg
		org	2000h
start:	

		ld	A,B
		add	A, A		; multiplica por 2	
		ld	B,A		
		
		ld	A,C
		add	A, A		; multiplica por 2	
		add	A,A		; multiplica por 4
		ld	C,A		
		
		ld	A,D
		add	A, A		; multiplica por 2	
		add	A,A		; multiplica por 4
		add	A,D		; multiplica por 5
		ld	D,A		
		

		rst  	38h			; halt           
		end  	start


