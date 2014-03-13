; ORGANIZACION BASICA DE LA COMPUTADORA - TP7
; < tp07.asm >
; Contiene subrutinas para el uso de vectores
; (repeticiones, insertar, borrar, ordenar, invertir, ocurrencias)
; y subrutinas matematicas varias
; (Fibonacci, multiplicacion, division, sucesion 2*Raiz(n), 
; desviacion estandar, producto de matrices, suma larga con DAA)

		Cseg

; ##################################################################
; Fibonacci
; Escribe los primeros N terminos de la sucesion de Fibonacci, 
; donde N es el valor del registro A, en la direccion apuntada por IX.
; Se detiene cuando se produce overflow (N >= 16).
; No altera registros ni flags. Solo escribe N direcciones de memoria.

Fibonacci:	push IX
		push BC
		push AF		; resguardos

		ld B, 2		; B = 2, IX = direccion de rta
		inc A		; A = N+1
	; este ciclo carga 0, 1 o 2 terminos "iniciales"
Fibonacci_inic:	dec A
		jp Z, Fibonacci_fin
		ld (IX), 1
		inc IX
		djnz Fibonacci_inic

		ld B, A
		jr Fibonacci_djnz
	; este ciclo agrega los demas terminos si N >= 3
Fibonacci_ciclo:	ld A, (IX-2)
		add A, (IX-1)
		jp PE, Fibonacci_fin	; si hay overflow, terminar
		ld (IX), A
		inc IX
Fibonacci_djnz:	djnz Fibonacci_ciclo

Fibonacci_fin:	pop AF		; restauraciones
		pop BC
		pop IX
		ret

; ##################################################################
; sinRepet
; Elimina las repeticiones de un vector ordenado apuntado por IX.
; La longitud del vector se indica en la zona apuntada por IY,
; que se actualiza. No altera registros ni flags.

sinRepet:	push BC
		push AF
		push IX
		push IY

		ld C, 0		; C = contador de nueva long.
		ld A, (IY)
		cp 2		; validacion de long. de vector >=2
		jp M, sinRepet_fin
		inc C
		dec A
		ld B, A		; B = long. vector - 1
		push IX
		pop IY		; "ld IY, IX"
		ld A, (IX)	; A=(IX) en casi toda la subrutina

sinRepet_ciclo:	inc IY
		cp (IY)		; inc IY hasta que (IX)!=(IY)
		jr Z, sinRepet_djnz
		inc IX		; luego pasa al siguiente elem,
		ld A, (IY)	; donde carga el elem. no repetido
		ld (IX), A
		inc C		; incrementa la nueva long. y recicla
sinRepet_djnz:	djnz sinRepet_ciclo	; hasta recorrer todo el vector

sinRepet_fin:	pop IY
		ld (IY), C
		pop IX
		pop AF
		pop BC
		ret

; ##################################################################
; InsertaEnOrden
; Inserta el elemento con el valor del registro A en el vector ordenado
; apuntado por IX (de longitud apuntada por IY), manteniendo el orden.
; No altera registros ni flags.

InsertaEnOrden:	push IY
		push IX
		push BC
		push AF		; resguardos

		ld B, 0
		ld C, (IY)	; BC = longitud del vector
		inc (IY)	; actualiza la nueva longitud
		push IX
		pop IY		; "ld IY, IX"
		add IY, BC	; si A >= ultimo elem, cargar al final
		dec IY		; (sino A < algun elem)
		cp (IY)
		jp P, InsertaEnOrden_fin

		ld B, C		; B indicara cuantos elementos correr
		inc B
		dec IX
	; este ciclo busca el primer elemento mayor que A
InsertaEnOrden_ciclo:	inc IX
		dec B
		cp (IX)
		jp P, InsertaEnOrden_ciclo

	; este ciclo corre los ultimos elementos (tantos como sea B)
InsertaEnOrden_corre:	ld C, (IY)
		ld (IY+1), C
		dec IY
		djnz InsertaEnOrden_corre
InsertaEnOrden_fin:	ld (IY+1), A

		pop AF	; restauraciones
		pop BC
		pop IX
		pop IY
		ret

; ##################################################################
; BorraElem
; Borra todos los elementos iguales al valor del registro A en el 
; vector desordenado apuntado por IX (de longitud apuntada por IY).
; No altera registros ni flags.

BorraElem:	push IX
		push DE
		push BC
		push IY
		push AF		; resguardos

		ld E, 0		; E = long. vector con elem borrado
		ld B, (IY)	; B = long. vector original
		push IX
		pop IY		; "ld IY, IX"
		dec IX
		dec IY
		inc B
		jr BorraElem_djnz
BorraElem_ciclo:	inc IY
		cp (IY)		; avanza mientras (IY)=A
		jr Z, BorraElem_djnz
		inc IX		; avanza en IX
		inc E		; incrementa la nueva long. vector
		ld C, (IY)	; (IX)=(IY) siempre que (IY)!=A
		ld (IX), C
BorraElem_djnz:	djnz BorraElem_ciclo	; recicla hasta recorrer todo

		pop AF		; restauraciones
		pop IY
		ld (IY), E
		pop BC
		pop DE
		pop IX
		ret

; ##################################################################
; Burbujeo
; Ordena los elementos del vector apuntado por IX (de longitud 
; apuntada por IY) por el metodo de burbujeo.
; No altera registros ni flags.

Burbujeo:	push IY
		push DE
		push BC
		push AF		; resguardos
		push IX

		ld D, (IY)
		dec D		; D = long - 1
		ld E, 0		; E = iterador loop externo
Burbujeo_loop:	inc E
		ld A, (IY)
		sub E
		ld B, A		; B = long - E = iterador ciclo
		pop IX
		push IX		; restaura IX
		dec IX
Burbujeo_ciclo:	inc IX
		ld A, (IX)	; avanza y compara dos elem contiguos
		cp (IX+1)
		jp M, Burbujeo_sigue
		ld C, (IX+1)	; si (IX)>(IX+1) los intercambia
		ld (IX+1), A
		ld (IX), C
Burbujeo_sigue:	djnz Burbujeo_ciclo	; recicla hasta recorrer todo
		dec D
		jp NZ, Burbujeo_loop

		pop IX
		pop AF		; restauraciones
		pop BC
		pop DE
		pop IY
		ret

; ##################################################################
; Invertir
; Invierte el orden del vector apuntado por IX (de longitud apuntada
; por IY). No altera registros ni flags.

Invertir:	push IY
		push IX
		push BC
		push AF		; resguardos

		ld C, (IY)
		push IX
		pop IY
		add IY, BC
		dec IY		; IY=ultimo elemento
		srl C		; C = [long vector / 2]
		ld B, C
		inc B
		jr Invertir_djnz
Invertir_ciclo:	ld A, (IX)
		ld C, (IY)
		ld (IY), A
		ld (IX), C	; se intercambian las puntas (IX),(IY)
		inc IX		; y se acercan al medio del vector
		dec IY
Invertir_djnz:	djnz Invertir_ciclo

		pop AF		; restauraciones
		pop BC
		pop IX
		pop IY
		ret

; ##################################################################
; Ocurrencias
; Devuelve en la zona apuntada por HL la cantidad de ocurrencias del 
; elemento de valor almacenado en el registro A en el vector apuntado 
; por IX (de longitud apuntada por IY). No altera registros ni flags.

Ocurrencias:	push IX
		push BC
		push AF		; resguardos

		ld B, (IY)	; B = long. vector
		ld C, 0		; C = contador de ocurrencias
		dec IX
		inc B
		jr Ocurrencias_s
Ocurrencias_c:	cp (IX)	; compara A con (IX)
		jp NZ, Ocurrencias_s
		inc C		; si son iguales cuenta (con C)
Ocurrencias_s:	inc IX		; sino avanza y recicla hasta
		djnz Ocurrencias_c ; recorrer todo el vector
		ld (HL), C	; guarda el resultado en (HL)

		pop AF		; restauraciones
		pop BC
		pop IX

; ##################################################################
; MltDE
; Multiplica D*E dejando el resultado en DE (16 bits). 
; Pueden ser valores negativos. No altera registros ni flags.

MltDE:		pop HL
		pop AF
		ld H, 0
		ld A, D
		cp 0	; evalua si D es negativo
		jp P, MltDE_Dnoneg
		inc H
		neg	; en ese caso, inc H y complementa D
		ld D, A
MltDE_Dnoneg:	ld A, E
		cp 0	; evalua si E es negativo
		jp P, MltDE_Enoneg
		inc H
		neg	; en ese caso, inc H y complementa E
		ld E, A
MltDE_Enoneg:	mlt DE	; multiplica y deja DE=D*E en valor absoluto
		srl H	; si era D<0 xor E<0, hay que complementar DE
		jr NC, MltDE_fin	; sino listo
		ld HL, 0
		add A, 0	; apaga el carry antes de sbc
		sbc HL, DE	; HL=0-DE
		ld E, L		; deja el resultado en DE
		ld D, H
MltDE_fin:	pop AF
		pop HL
		ret

; ##################################################################
; ParImpar
; Separa en dos vectores (apuntados por IX e IY respectivamente) 
; los elementos pares e impares del vector apuntado por HL.
; Recibe por stack las longitudes de los tres vectores en el 
; siguiente orden: Vector "inicial", vector "par" y vector "impar".

ParImpar:	ld (ParImpar_bkp), BC
		ld (ParImpar_bkp+2), DE
		ld (ParImpar_bkp+4), HL
		pop BC		; levanta dir de ret
		pop HL		; HL = dir de long de vector inicial
		ld (ParImpar_bkp+6), DE
		pop DE		; dir de long de vector par
		ld (ParImpar_bkp+8), DE
		pop DE		; dir de long de vector impar
		push BC		; devuelve dir de ret
		push IY
		push IX
		push AF		; resguardos

		ld B, (HL)	; B = long del vector inicial
		ld DE, 0	; inicializa D=long par, E=long impar
		ld HL, (ParImpar_bkp+4)	; HL apunta al vector inicial
		inc B
		dec HL
		jr ParImpar_sigue

ParImpar_ciclo:	ld A, (HL)
		srl A		; salta si el bit 0 es 1 (impar)
		jp C, ParImpar_impar
		ld (IX), A
		inc IX
		inc D
		jr ParImpar_sigue
ParImpar_impar:	ld (IY), A
		inc IY
		inc E
ParImpar_sigue:	inc HL
		djnz ParImpar_ciclo

		ld HL, (ParImpar_bkp+6)
		ld (HL), D
		ld HL, (ParImpar_bkp+8)
		ld (HL), E

		ld BC, (ParImpar_bkp)
		ld DE, (ParImpar_bkp+2)
		ld HL, (ParImpar_bkp+4)
		pop AF		; restauraciones
		pop IX
		pop IY
		ret

		Dseg
ParImpar_bkp:	ds 10
		Cseg


; ##################################################################
; DivDE
; Divide D/E, dejando el cociente en E y el resto en D. (E no nulo!)
; Solo modifica los registros D y e. No altera los flags.

DivDE:		push AF
		push BC		; resguardos

		ld C, 0		; C acumulara el cociente
		ld A, D
DivDE_ciclo:	sub E		; A = A-E sucesivamente
		jp M, DivDE_fin	; si el resultado es negativo, se paso
		inc C		; C cuenta cuantas restas se hicieron
		jr DivDE_ciclo	; recicla hasta que A<0

DivDE_fin:	add A, E	; A en E porque se paso
		ld D, A		; en D queda el resto
		ld E, C		; y en E el cociente

		pop BC		; restauraciones
		pop AF
		ret

; ##################################################################
; Raiz
; Calcula raiz cuadrada del valor >=0 almacenado en el registro A,
; dejando el resultado en el mismo registro. Para la aproximacion,
; se utiliza la siguiente formula recursiva con 8 iteraciones:
; A_n+1 = (A_n + A/A_n) / 2 donde A es el valor original de A.
; No altera otro registro que A, ni flags.

Raiz:		push DE
		push BC
		push AF		; resguardos

		cp 2
		jp M, Raiz_fin	; si A<2, Raiz(A)=A
		ld C, A		; resguarda el A original en C
		ld B, 8		; B = iteraciones
		srl A		; el primer termino es A/2

Raiz_ciclo:	ld D, C
		ld E, A
		call DivDE	; E = C/A
		add A, E	; A = A+E
		srl A		; A = A/2
		djnz Raiz_ciclo

		pop BC		; deja el resultado en A
		ld B, A
		push BC
Raiz_fin:	pop AF		; restauraciones
		pop BC
		pop DE
		ret

; ##################################################################
; Suma2RaizN
; Suma los primeros N terminos de la sucesion 2*Raiz(n), donde N
; es el valor del registro A, dejando el resultado en la direccion
; apuntada por IX. No controla el overflow (para N>=36 aprox.).
; No altera registros ni flags.

Suma2RaizN:	push DE
		push BC
		push AF

		ld D, 0		; D acumulara la sumatoria
		cp 0
		jp Suma2RaizN_fin
		ld B, A		; B son las iteraciones
		ld C, 1		; C sera el n para la sucesion a(n)

Suma2RaizN_ciclo:	ld A, C
		call Raiz	; Raiz(n)
		add A, D	; acumula la sumatoria
		ld D, A
		inc C		; incrementa n (sig termino) y recicla
		djnz Suma2RaizN_ciclo

		sla D		; duplica para resultado final
Suma2RaizN_fin:	ld (IX), D
		pop AF
		pop BC
		pop DE
		ret

; ##################################################################
; DesvEst
; Calcula la desviacion estandar de los elementos del vector apuntado
; por IX, segun la formula: DesvEst = Raiz(Suma((A_i-A_media)^2)/N)
; La longitud N del vector esta apuntada por IY. Deja el resultado 
; en la zona apuntada por HL. No altera registros ni flags.

DesvEst:	push IX
		push DE
		push BC
		push AF		; resguardos
		push HL

		ld A, (IY)
		cp 0		; considera caso vector de long.0
		jp Z, DesvEst_fin
		ld B, A		; B iteraciones
		ld D, 0		; D acumulador suma elem del vector

DesvEst_ciclo:	ld A, (IX)
		add A, D
		ld D, A		; acumula la sumatoria de elem en D
		inc IX		; pasa al sig elem y recicla hasta
		djnz DesvEst_ciclo	; recorrer todo el vector

		ld B, (IY)	; restaura en B la long. del vector
		ld E, B		; y la copia en E
		call DivDE	; Suma elem / Total elem = Promedio
		ld C, E		; deja el promedio en C
		ld HL, 0	; HL acumulara suma (x_i-x_prom)^2
		dec IX		; se recorrera al vector para atras

DesvEst_loop:	ld A, (IX)
		sub A, C	; resta a cada elem el promedio
		jp P, DesvEst_noneg
		neg
DesvEst_noneg:	ld D, A
		ld E, A
		mlt DE		; eleva el resultado al cuadrado
		add HL, DE	; y lo acumula en HL
		dec IX		; pasa al elem anterior y recicla
		djnz DesvEst_loop	; hasta recorrer todo

		; se supone que la suma acumulada en HL < 0080h
		ld D, L		; D = sumatoria (x_i-x_prom)^2
		ld E, (IY)	; E = long. vector
		call DivDE
		ld A, E		; carga la varianza en A
		call Raiz	; deja la desviacion estandar en A

DesvEst_fin:	pop HL
		ld (HL), A	; guarda el resultado en (HL) original
		pop AF		; restauraciones
		pop BC
		pop DE
		pop IX
		ret

; ##################################################################
; Mlt1NxNN
; Calcula el producto entre una matriz de 1xN (apuntada por IX) y 
; una matriz cuadrada de NxN (apuntada por IY), dejando la matriz 
; resultante de 1xN en la direccion apuntada por HL. N esta dado por 
; el valor del registro A. No altera registros ni flags.

Mlt1NxNN:	push HL
		push DE
		push BC
		push AF		; resguardos
		push IY
		push IX

		cp 0		; considera caso vector de long.0
		jp Z, Mlt1NxNN_fin
		ld B, A		; B iteraciones por columnas
		ld C, A		; C iteraciones por filas
		ld (HL), 0

Mlt1NxNN_ciclo:
		ld D, (IX)
		ld E, (IY)
		mlt DE

		ld D, A		; resguarda A
		ld A, (HL)
		add A, E	; (HL) = (HL) + (IX)*(IY)
		ld (HL), A
		ld A, D		; restaura A

		inc IX		; IX=IX+1 (sig. columna)
		ld D, 0
		ld E, A
		add IY, DE	; IY=IY+cant.columnas (sig. fila)

		djnz Mlt1NxNN_ciclo

		ld D, A		; resguarda A
		sub A, C
		inc A
		ld E, A		; E = desplazamiento a sig. fila
		ld A, D		; restaura A

		ld D, 0
		pop IX
		pop IY
		push IY		; restaura IX e IY
		push IX		; al comienzo de cada vector
		add IY, DE	; y desplaza IY a la sig. fila
		inc HL
		ld (HL), 0	; pasa al sig elem de rta y lo anula

		ld B, A		; restaura B iteraciones
		dec C
		jp NZ, Mlt1NxNN_ciclo

Mlt1NxNN_fin:	pop IX
		pop IY
		pop AF		; restauraciones
		pop BC
		pop DE
		pop HL
		ret

; ##################################################################
; MltNNxNN
; Calcula el producto entre dos matrices cuadradas de NxN (apuntadas 
; por IX e IY), dejando la matriz resultante de NxN en la direccion 
; apuntada por HL. N esta dado por el valor del registro A.
; No altera registros ni flags.

MltNNxNN:	push IX
		push HL
		push DE
		push BC
		push AF

		cp 0		; considera caso vector de long.0
		jp Z, MltNNxNN_fin
		ld B, A		; B iteraciones
		ld D, 0
		ld E, A		; DE = desplazamiento a sig. fila

MltNNxNN_ciclo:	call Mlt1NxNN
		add IX, DE
		add HL, DE
		djnz MltNNxNN_ciclo

MltNNxNN_fin:	pop AF
		pop BC
		pop DE
		pop HL
		pop IX
		ret

; ##################################################################
; SumaDAA
; Calcula la suma de dos enteros largos en "representacion BCD" 
; almacenados en las direcciones apuntadas por IX e IY.
; La cantidad de zonas de memoria ocupadas esta dada por A, y la
; direccion de respuesta por HL. No altera registros ni flags.
; (La respuesta ocupa un lugar mas que los vectores a sumar.)

SumaDAA:	push IY
		push IX
		push HL
		push BC
		push AF

		cp 0
		jp Z, SumaDAA_fin
		ld B, 0
		ld C, A
		dec C
		add IX, BC	; deja IX, IY y HL apuntados
		add IY, BC	; al byte menos significativo
		add HL, BC
		inc HL		; reservando uno mas para HL
		ld B, A		; B iteraciones
		add A, 0	; apaga el carry

SumaDAA_ciclo:	ld A, (IX)
		adc A, (IY)	; A=(IX)+(IY)+carry
		daa		; ajusta el resultado a "repr. BCD"
		ld (HL), A
		dec IX		; los dec de 16 bits no alteran flags
		dec IY		; por ende pasa al byte menos signif.
		dec HL		; sin riesgo de perder el carry de DAA
		djnz SumaDAA_ciclo

		ld A, 0
		adc A, 0	; A = 1 o 0 dependiendo del carry
		ld (HL), A	; carga el byte mas signif. de la rta

SumaDAA_fin:	pop AF
		pop BC
		pop HL
		pop IX
		pop IY
		ret

; ##################################################################
; ZONA DE PROGRAMA

start1:		ld SP, 0
		ld IX, SUCESION
	; cargar A manualmente
		call Fibonacci
		rst 38h

start2:		ld SP, 0
		ld IX, VECTOR
		ld IY, DIM
		call sinRepet
		rst 38h

start3:		ld SP, 0
		ld IX, VECTOR
		ld IY, DIM
	; cargar A manualmente
		call InsertaEnOrden
		rst 38h

start4:		ld SP, 0
		ld IX, VECTOR
		ld IY, DIM
	; cargar A manualmente
		call BorraElem
		rst 38h

start5:		ld SP, 0
		ld IX, VECTOR_DESORD
		ld IY, DIM_DESORD
		call Burbujeo
		rst 38h

start11:	ld SP, 0
		ld IX, matriz1
		ld IY, rta
		ld (IY), 9
		ld HL, rta+1
		call DesvEst
		rst 38h

start12:	ld SP, 0
		ld IX, matriz1
		ld IY, matriz2
		ld A, dimension
		ld HL, rta
		call MltNNxNN
		rst 38h

start13:	ld SP, 0
		ld IX, numdaa1
		ld IY, numdaa2
		ld A, dimension
		ld HL, rta
		call SumaDAA
		rst 38h

		Dseg

NRO:		equ 14
SUCESION:	db 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
VECTOR:		db 10,10,30,35,35,35,52,52,0
DIM:		db $-VECTOR-1
VECTOR_DESORD:	db 15h,10h,20h,50h,12h
DIM_DESORD:	db $-VECTOR_DESORD
matriz1:	db 1,2,3,4,5,6,7,8,9
matriz2:	db 2,0,0,0,2,0,0,0,2
rta:		db 0,0,0,0,0,0,0,0,0
dimension:	equ 3
numdaa1:	db 53h,17h,83h
numdaa2:	db 78h,19h,25h

		end start5
