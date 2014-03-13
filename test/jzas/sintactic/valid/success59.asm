		aseg
		org 3000h

swapMemo:	ld (aux), A	;aux=A
		ld A, (IX)
		ld (aux+1), A	;aux+1=(IX)
		ld A, (IY)
		ld (IX), A	;(IX)=(IY)
		ld A, (aux+1)
		ld (IY), A	;(IY)=aux+1
		ld A, (aux)
		jr aux+2
aux:		ds 2
		ret

start:		ld IX, dirIX
		ld IY, dirIY
		call swapMemo
		rst 38h

dirIX:		db 0ABh
dirIY:		db 0CDh
		end start
