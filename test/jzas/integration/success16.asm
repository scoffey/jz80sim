	ld A, (1)			; ld A, (0001)
	ld A, (1+2)			; ld A, (0003)
	ld A, 1+2			; ld A, 03
	ld A, (1+2+3)			; ld A, (0006)
	ld A, 1+2+3			; ld A, 06
	ld A, 1+(2+3)			; ld A, 06
	ld A, (1 + (2 + 3))		; ld A, (0006)
	ld A, ((1 + (2 + 3)))		; ld A, (0006)
	ld A, 1+((2+ (((3)))))		; ld A, 06
	ld A, (((1+((2+ (((3))))))))	; ld A, (0006)
